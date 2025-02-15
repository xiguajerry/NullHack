package love.xiguajerry.nullhack.modules.impl.player

import love.xiguajerry.nullhack.event.api.handler
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.PacketEvent
import love.xiguajerry.nullhack.event.impl.player.OnUpdateWalkingPlayerEvent
import love.xiguajerry.nullhack.event.impl.player.PlayerClickBlockEvent
import love.xiguajerry.nullhack.event.impl.render.Render2DEvent
import love.xiguajerry.nullhack.event.impl.render.Render3DEvent
import love.xiguajerry.nullhack.event.impl.world.WorldEvent
import love.xiguajerry.nullhack.gui.NullHackGui
import love.xiguajerry.nullhack.manager.managers.CombatManager
import love.xiguajerry.nullhack.manager.managers.HotbarSwitchManager
import love.xiguajerry.nullhack.manager.managers.PlayerPacketManager.sendPlayerPacket
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.mixins.accessor.IPlayerMoveC2SPacketAccessor
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.modules.impl.player.PacketMine.SwitchMode.*
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.NonNullContext
import love.xiguajerry.nullhack.utils.extension.*
import love.xiguajerry.nullhack.graphics.animations.AnimationFlag
import love.xiguajerry.nullhack.graphics.animations.Easing
import love.xiguajerry.nullhack.graphics.buffer.Render3DUtils
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.utils.inventory.*
import love.xiguajerry.nullhack.utils.inventory.pause.MainHandPause
import love.xiguajerry.nullhack.utils.inventory.pause.withPause
import love.xiguajerry.nullhack.utils.math.RotationUtils
import love.xiguajerry.nullhack.utils.math.fastCeil
import love.xiguajerry.nullhack.utils.math.scale
import love.xiguajerry.nullhack.utils.math.sq
import love.xiguajerry.nullhack.utils.math.vectors.Vec2f
import love.xiguajerry.nullhack.utils.math.vectors.toVec3Center
import love.xiguajerry.nullhack.utils.timing.TickTimer
import love.xiguajerry.nullhack.utils.world.BlockUtils
import love.xiguajerry.nullhack.utils.world.BlockUtils.getPlaceSide
import love.xiguajerry.nullhack.utils.world.BlockUtils.place
import love.xiguajerry.nullhack.utils.world.CrystalUtils
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.boss.enderdragon.EndCrystal
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.pow

object PacketMine : Module("Packet Mine", category = Category.PLAYER) {
    private val delay by setting("Delay", 50, 0..500, 25)
    private val damage by setting("Damage", 1.0f, 0.0f..2.0f, 0.01f)
    private val range by setting("Range", 6f, 2f..10f, 0.1f)
    private val maxBreak by setting("Max Break", 3, 0..20, 1)
    private val instant by setting("Instant", true)
    private val cancelPacket by setting("Cancel Packet", false)
    private val wait by setting("Wait", true, { !instant })
    private val mineAir by setting("Mine Air", true, { wait })
    private val distanceCancel by setting("Distance Cancel", false)
    private val switchMode by setting("Switch Mode", INVENTORY)
    private val checkGround by setting("Check Ground", true)
    private val groundOnly by setting("Ground Only", true)
    private val doubleBreak by setting("Double Break", true)
    private val pauseWhileUsing by setting("Pause While Using", true)
    private val swing by setting("Swing", true)
    private val endSwing by setting("End Swing", true)
    private val bypassGround by setting("Bypass Ground", true)
    private val bypassTime by setting("Bypass Time", 400, 0..2000, 20, { bypassGround })
    private val rotate by setting("Rotate", true)
    private val endRotate by setting("End Rotate", true)
    private val switchReset by setting("Switch Reset", false)
    private val crystal by setting("Crystal", false)
    private val onlyHeadBomber by setting("Only Cev", true, { crystal })
    private val awaitPlace by setting("Await Place", true, { crystal })
    private val afterBreak by setting("After Break", true, { crystal })
    private val checkDamage by setting("Check Progress", true, { crystal })
    private val crystalDamage by setting("Progress", 0.9f, 0.0f..1.0f, 0.01f, { crystal && checkDamage })
    private val placeDelay by setting("Place Delay", 100, 0..1000, 50, { crystal })
    private val startColor by setting("Start Color", ColorRGBA.WHITE.alpha(100))
    private val endColor by setting("End Color", ColorRGBA.WHITE.alpha(100))
    private val doubleColor by setting("Double Color", ColorRGBA(88, 94, 255, 100), { doubleBreak })
    private val text by setting("Text", true)
    private val box by setting("Box", true)
    private val outline by setting("Outline", true)

    val godBlocks: List<Block> = listOf(
        Blocks.COMMAND_BLOCK,
        Blocks.LAVA_CAULDRON,
        Blocks.LAVA,
        Blocks.WATER_CAULDRON,
        Blocks.WATER,
        Blocks.BEDROCK,
        Blocks.BARRIER,
        Blocks.END_PORTAL,
        Blocks.NETHER_PORTAL,
        Blocks.END_PORTAL_FRAME
    )

    var lastSlot = -1
    var breakPos: BlockPos? = null
    var secondPos: BlockPos? = null
    var progress = 0.0
    private val firstTimer = TickTimer()
    private val secondTimer = TickTimer()
    var breakLength1 = 1000f
    var breakLength2 = 1000f
    private val firstAnim = AnimationFlag(Easing.OUT_CUBIC) { breakLength1 }
    private val secondAnim = AnimationFlag(Easing.OUT_CUBIC) { breakLength2 }
    private var breakNumber = 0
    private var startMine = false
    private val delayTimer = TickTimer()
    private val placeTimer = TickTimer()
    private var sendGroundPacket = false
    private val df = DecimalFormat("0.0")

    init {
        handler<WorldEvent.Load> {
            startMine = false
            breakPos = null
            secondPos = null
        }

        nonNullHandler<Render2DEvent> {
            if (!text) return@nonNullHandler
            breakPos?.let {
                val slot = getTool(breakPos!!) ?: player.hotbarSlots[HotbarSwitchManager.serverSideHotbar]
                val breakTime = getBreakTime(breakPos!!, slot)
                val text = if (isAir(breakPos!!)) "Waiting..."
                else if (firstTimer.passed < breakTime) df.format(firstTimer.passed / breakTime * 100) + "%"
                else "100.0%"
                if (RotationUtils.getRotationDiff(
                        Vec2f(mc.entityRenderDispatcher.camera.yRot, mc.entityRenderDispatcher.camera.xRot),
                        RotationUtils.getRotationTo(it.toVec3Center())) < mc.options.fov().get()) {
                    val screenPos = Render3DUtils.worldToScreen(it.toVec3Center())
                    val alpha = (255.0f * firstAnim.getAndUpdate(100f) / 100).toInt()
                    val color = if (firstAnim.getAndUpdate(100f) / 100 >= 1.0f)
                        ColorRGBA(255, 255, 255)
                    else ColorRGBA(255, 255, 255, alpha)

                    UnicodeFontManager.CURRENT_FONT.drawStringWithShadow(
                        text,
                        screenPos.x.toFloat() - UnicodeFontManager.CURRENT_FONT.getWidth(text, 2.0f) * 0.5f,
                        screenPos.y.toFloat() - UnicodeFontManager.CURRENT_FONT.getHeight(2.0f) * 0.5f,
                        color.awt,
                        2.0f
                    )
                }
            }
        }

        nonNullHandler<Render3DEvent> {
            if (!player.isCreative) {
                if (secondPos != null) {
                    if (isAir(secondPos!!)) {
                        secondPos = null
                    } else {
                        val size = secondAnim.getAndUpdate(100f)
                        if (box) Render3DUtils.drawBox(AABB(secondPos).scale(size / 2), doubleColor)
                        if (outline) {
                            Render3DUtils.drawBoxOutline(AABB(secondPos).scale(size / 2), 1f, doubleColor.alpha(255))
                        }
                    }
                }
                if (breakPos != null) {
                    val slot = getTool(breakPos!!) ?: player.hotbarSlots[HotbarSwitchManager.serverSideHotbar]
                    val breakTime = getBreakTime(breakPos!!, slot)
                    progress = (firstTimer.passed / breakTime).toInt().toDouble()
                    breakLength1 = getBreakTime(breakPos!!, slot).toFloat()
                    val size = firstAnim.getAndUpdate(100f)
                    val color = interpolateColor(size / 100f)
                    if (box) Render3DUtils.drawBox(AABB(breakPos).scale(size / 2), color)
                    if (outline) {
                        Render3DUtils.drawBoxOutline(AABB(breakPos).scale(size / 2), 1f, color.alpha(255))
                    }
                } else {
                    progress = 0.0
                }
            } else {
                progress = 0.0
            }
        }

        nonNullHandler<PlayerClickBlockEvent> { event ->
            if (!player.isCreative) {
                mine(event.pos)
                event.cancel()
            }
        }
        
        nonNullHandler<PacketEvent.Send> { event ->
            if (event.packet is ServerboundMovePlayerPacket) {
                if (bypassGround && breakPos != null && !isAir(breakPos!!) && bypassTime > 0
                    && (breakPos!!.center.distanceToSqr(player.eyePosition).toFloat() <= (range + 2).sq)) {
                    val slot = getTool(breakPos!!) ?: player.hotbarSlots[HotbarSwitchManager.serverSideHotbar]
                    val breakTime: Double = (getBreakTime(breakPos!!, slot) - bypassTime)
                    if (breakTime <= 0 || firstTimer.tick(breakTime.toLong())) {
                        sendGroundPacket = true
                        (event.packet as IPlayerMoveC2SPacketAccessor).setOnGround(true)
                    }
                } else {
                    sendGroundPacket = false
                }
            } else if (event.packet is ServerboundSetCarriedItemPacket) {
                if (event.packet.slot != lastSlot) {
                    lastSlot = event.packet.slot
                    if (switchReset) {
                        startMine = false
                        firstTimer.reset()
                        firstAnim.forceUpdate(0f, 0f)
                    }
                }
            } else if (event.packet !is ServerboundPlayerActionPacket) {
                return@nonNullHandler
            } else if (event.packet.action == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
                if (breakPos == null || event.packet.pos != breakPos) {
                    if (cancelPacket) event.cancel()
                    return@nonNullHandler
                }
                startMine = true
            } else if (event.packet.action == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
                if (breakPos == null || event.packet.pos != breakPos) {
                    if (cancelPacket) event.cancel()
                    return@nonNullHandler
                }
                if (!instant) {
                    startMine = false
                }
            }
        }

        nonNullHandler<OnUpdateWalkingPlayerEvent.Pre> {
            update()
        }

        onDisabled {
            startMine = false
            breakPos = null
        }
    }

    context(NonNullContext)
    fun mine(pos: BlockPos) {
        if (player.isCreative) {
//            interaction.attackBlock(pos, BlockUtils.getClickSide(pos))
            return
        }
        if (godBlocks.contains(world.getBlockState(pos).block)) {
            return
        }
        if (pos == breakPos) {
            return
        }
        if (BlockUtils.getClickSideStrict(pos) == null) {
            return
        }
        if (player.eyePosition.distanceToSqr(pos.center) > range.sq) {
            return
        }
        breakPos = pos
        breakNumber = 0
        startMine = false
        startMine()
    }

    context(NonNullContext)
    private fun update() {
        if (player.isDeadOrDying) {
            secondPos = null
        }
        if (secondPos != null && secondTimer
            .tick(getBreakTime(secondPos!!, player.hotbarSlots[HotbarSwitchManager.serverSideHotbar], 1.1).toInt())) {
            secondPos = null
        }
        if (secondPos != null && isAir(secondPos!!)) secondPos = null
        if (player.isCreative) {
            startMine = false
            breakNumber = 0
            breakPos = null
            return
        }
        if (breakPos == null) {
            breakNumber = 0
            startMine = false
            return
        }
        if (isAir(breakPos!!)) {
            breakNumber = 0
        }
        if (breakNumber > maxBreak - 1 && maxBreak > 0 || !wait && isAir(breakPos!!) && !instant) {
            if (breakPos == secondPos) {
                secondPos = null
            }
            startMine = false
            breakNumber = 0
            breakPos = null
            return
        }
        if (godBlocks.contains(world.getBlockState(breakPos).block)) {
            breakPos = null
            startMine = false
            return
        }
        if (pauseWhileUsing && player.isUsingItem) {
            return
        }
        if (player.eyePosition.distanceToSqr(breakPos!!.center) > range.sq) {
            if (distanceCancel) {
                startMine = false
                breakNumber = 0
                breakPos = null
            }
            return
        }
        if (switchMode == NONE && mc.screen != null && mc.screen !is ChatScreen
            && mc.screen !is InventoryScreen && mc.screen !is NullHackGui<*>) {
            return
        }
        val slot = getTool(breakPos!!) ?: player.hotbarSlots[HotbarSwitchManager.serverSideHotbar]

        if (isAir(breakPos!!)) {
            if (shouldCrystal(breakPos!!)) {
                for (facing in Direction.entries.toTypedArray()) {
                    attackCrystal(breakPos!!.relative(facing), rotate, true)
                }
            }

            breakNumber = 0
        } else if (CrystalUtils.canPlaceCrystal(breakPos!!.above())) {
            if (shouldCrystal(breakPos!!)) {
                if (placeTimer.tick(placeDelay)) {
                    if (checkDamage) {
                        if (firstTimer.passed / getBreakTime(breakPos!!, slot) >= crystalDamage) {
                            if (!placeCrystal()) return
                        }
                    } else {
                        if (!placeCrystal()) return
                    }
                } else if (startMine) {
                    return
                }
            }
        }
        
        if (!delayTimer.tick(delay)) return
        if (startMine) {
            if (isAir(breakPos!!)) {
                return
            }
            if (groundOnly && !player.onGround()) return
            if (firstTimer.tick(getBreakTime(breakPos!!, slot).toLong())) {

                if (endRotate) {
                    sendPlayerPacket {
                        rotate(RotationUtils.getRotationTo(breakPos!!.toVec3Center()))
                    }
                }

                if (endSwing) player.swing(InteractionHand.MAIN_HAND)
                if (switchMode != NONE) {
                    swap(slot) {
                        netHandler.send(
                            ServerboundPlayerActionPacket(
                                ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK,
                                breakPos,
                                BlockUtils.getClickSide(breakPos!!)
                            )
                        )
                    }
                } else netHandler.send(
                    ServerboundPlayerActionPacket(
                        ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK,
                        breakPos,
                        BlockUtils.getClickSide(breakPos!!)
                    )
                )
                breakNumber++
                delayTimer.reset()
                if (afterBreak && shouldCrystal(breakPos!!)) {
                    for (facing in Direction.entries) {
                        attackCrystal(breakPos!!.relative(facing), rotate, true)
                    }
                }
            }
        } else {
            if (!mineAir && isAir(breakPos!!)) {
                return
            }
            startMine()
        }
    }

    context(NonNullContext)
    private fun startMine() {
        firstTimer.reset()
        firstAnim.forceUpdate(0f, 0f)
        if (rotate) {
            val vec3i = BlockUtils.getClickSide(breakPos!!)!!.unitVec3
            sendPlayerPacket {
                rotate(RotationUtils.getRotationTo(
                    breakPos!!.center.add(
                        Vec3(vec3i.x * 0.5,
                            vec3i.y * 0.5,
                            vec3i.z * 0.5
                        )
                    )
                ))
            }
        }
        netHandler.send(
            ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                breakPos,
                BlockUtils.getClickSide(breakPos!!)
            )
        )
        if (doubleBreak) {
            if (secondPos == null || isAir(secondPos!!)) {
                val slot = getTool(breakPos!!) ?: player.hotbarSlots[HotbarSwitchManager.serverSideHotbar]
                val breakTime = (getBreakTime(breakPos!!, slot, 1.0))
                secondAnim.forceUpdate(0f, 0f)
                breakLength2 = breakTime.toFloat()
                secondTimer.reset()
                secondPos = breakPos
            }
            netHandler.send(
                ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK,
                    breakPos,
                    BlockUtils.getClickSide(breakPos!!)
                )
            )
            netHandler.send(
                ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                    breakPos,
                    BlockUtils.getClickSide(breakPos!!)
                )
            )
        }
        if (swing) {
            player.swing(InteractionHand.MAIN_HAND)
        }
    }

    context(NonNullContext)
    private fun placeCrystal(): Boolean {
        findCrystal()?.let {
            swap(it) {
                place(breakPos!!.above(), rotate = rotate)
                placeTimer.reset()
            }
            return !awaitPlace
        }
        return true
    }

    context(NonNullContext)
    private fun swap(slot: Slot, func: () -> Unit) {
        when (switchMode) {
            NONE -> return
            LEGIT -> MainHandPause.withPause(PacketMine) {
                swapToSlot(slot as HotbarSlot)
                func()
            }
            GHOST, INVENTORY -> {
                HotbarSwitchManager.ghostSwitch(slot, func)
            }
        }
    }

    context (NonNullContext)
    private fun canPlace(pos: BlockPos) =
        getPlaceSide(pos, false, false) != null && isReplaceable(pos)

    context (NonNullContext)
    private fun isReplaceable(pos: BlockPos): Boolean {
        return world.getBlockState(pos).canBeReplaced()
    }

    context(NonNullContext)
    private fun attackCrystal(pos: BlockPos, rotate: Boolean, eatingPause: Boolean) {
        for (entity in world.getEntitiesOfClass(EndCrystal::class.java, AABB(pos))) {
            attackCrystal(entity, rotate, eatingPause)
            break
        }
    }

    context(NonNullContext)
    private fun attackCrystal(crystal: Entity, rotate: Boolean, usingPause: Boolean) {
        if (usingPause && player.usingItemHand != null) return
        netHandler.send(ServerboundInteractPacket.createAttackPacket(crystal, player.isShiftKeyDown))
        player.resetAttackStrengthTicker()
    }

    private fun BlockPos.isAbove(entity: Entity): Boolean {
        return x == entity.x.toInt() && y >= entity.y.toInt() + 1 && z == entity.z.toInt()
    }

    private fun shouldCrystal(pos: BlockPos): Boolean {
        return crystal && (!onlyHeadBomber || CombatManager.target?.let { pos.isAbove(it) } == true)
    }

    context(NonNullContext)
    private fun getTool(pos: BlockPos): Slot? {
        val result = when (switchMode) {
            LEGIT, GHOST -> {
                player.hotbarSlots.maxByOrNull { slot ->
                    val it = slot.item
                    val digSpeed = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.EFFICIENCY.entry, it).toFloat()
                    val destroySpeed = it.getDestroySpeed(world.getBlockState(pos))
                    digSpeed + destroySpeed
                }
            }
            INVENTORY -> {
                player.everySlots.maxByOrNull { slot ->
                    val it = slot.item
                    val digSpeed = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.EFFICIENCY.entry, it).toFloat()
                    val destroySpeed = it.getDestroySpeed(world.getBlockState(pos))
                    digSpeed + destroySpeed
                }
            }
            else -> null
        }
        return result
    }

    override fun getDisplayInfo(): Any {
        if (progress >= 1) {
            return "Done"
        }
        return df.format(progress * 100) + "%"
    }

    context(NonNullContext)
    private fun findCrystal(): Slot? {
        if (switchMode == LEGIT || switchMode == GHOST) {
            return player.hotbarSlots.firstItem(Items.END_CRYSTAL)
        } else if (switchMode == INVENTORY) {
            return player.everySlots.firstItem(Items.END_CRYSTAL)
        }
        return null
    }

    context(NonNullContext)
    private fun findBlock(block: Block): Slot? {
        if (switchMode == LEGIT || switchMode == GHOST) {
            return player.hotbarSlots.firstBlock(block)
        } else if (switchMode == INVENTORY) {
            return player.everySlots.firstBlock(block)
        }
        return null
    }

    private fun interpolateColor(quad: Float): ColorRGBA {
        val sR: Int = startColor.r
        val sG: Int = startColor.g
        val sB: Int = startColor.b
        val sA: Int = startColor.a

        val eR: Int = endColor.r
        val eG: Int = endColor.g
        val eB: Int = endColor.b
        val eA: Int = endColor.a
        return ColorRGBA(
            (sR + (eR - sR) * quad).toInt(),
            (sG + (eG - sG) * quad).toInt(),
            (sB + (eB - sB) * quad).toInt(),
            (sA + (eA - sA) * quad).toInt()
        )
    }

    context(NonNullContext)
    fun getBreakTime(pos: BlockPos, slot: Slot): Double {
        return 50.0 * ((pos.state.getDestroySpeed(world, pos) * 30
                / getDigSpeed(pos.state, slot.item)).fastCeil() + 1) / damage
    }

    context(NonNullContext)
    fun getBreakTime(pos: BlockPos, slot: Slot, damage: Double): Double {
        return 50.0 * ((pos.state.getDestroySpeed(world, pos) * 30
                / getDigSpeed(pos.state, slot.item)).fastCeil() + 1) / damage
    }

    context(NonNullContext)
    private fun canBreak(pos: BlockPos): Boolean {
        val blockState: BlockState = world.getBlockState(pos)
        val block = blockState.block
        return block.defaultDestroyTime() != -1f
    }

    context(NonNullContext)
    private fun getBlockStrength(position: BlockPos, itemStack: ItemStack): Float {
        val state: BlockState = world.getBlockState(position)
        val hardness = state.getDestroySpeed(world, position)
        if (hardness < 0) {
            return 0f
        }
        return if (!canBreak(position)) {
            getDigSpeed(state, itemStack) / hardness / 100f
        } else {
            getDigSpeed(state, itemStack) / hardness / 30f
        }
    }

    context(NonNullContext)
    private fun getDigSpeed(state: BlockState, itemStack: ItemStack): Float {
        var digSpeed = getDestroySpeed(state, itemStack)

        if (digSpeed > 1) {
            val efficiencyModifier = EnchantmentHelper.getItemEnchantmentLevel(
                Enchantments.EFFICIENCY.entry, itemStack
            )
            if (efficiencyModifier > 0 && !itemStack.isEmpty) {
                digSpeed += (efficiencyModifier.toDouble().pow(2.0) + 1).toFloat()
            }
        }
        if (player.hasEffect(MobEffects.DIG_SPEED))
            digSpeed *= 1 + (player.getEffect(MobEffects.DIG_SPEED)!!.amplifier + 1) * 0.2f

        if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            val miningFatigue: Float = when (player.getEffect(MobEffects.DIG_SLOWDOWN)!!.amplifier) {
                0 -> 0.3f
                1 -> 0.09f
                2 -> 0.0027f
                3 -> 8.1E-4f
                else -> 8.1E-4f
            }
            digSpeed *= miningFatigue
        }
        if (player.isUnderWater)
            digSpeed *= player.getAttribute(Attributes.SUBMERGED_MINING_SPEED)!!.value.toFloat()

        if (!player.onGround() && checkGround) digSpeed /= 5f

        return if (digSpeed < 0) 0f else digSpeed
    }

    fun getDestroySpeed(state: BlockState, stack: ItemStack, ): Float {
        val str = stack.getDestroySpeed(state).toDouble()
        val effect = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.EFFICIENCY.entry, stack)
        return max(str + if (str > 1.0) effect * effect + 1.0 else 0.0, 0.0).toFloat()
    }

    context(NonNullContext)
    private fun isAir(breakPos: BlockPos): Boolean {
        return world.isEmptyBlock(breakPos) || breakPos.block == Blocks.FIRE && hasCrystal(breakPos)
    }

    context(NonNullContext)
    private fun hasCrystal(pos: BlockPos): Boolean {
        for (entity in world.getEntitiesOfClass(EndCrystal::class.java, AABB(pos))) {
            if (!entity.isAlive || entity !is EndCrystal) continue
            return true
        }
        return false
    }

    fun isMine(pos:BlockPos, checkDoubleMine: Boolean = true): Boolean{
        if (!checkDoubleMine){
            return breakPos == pos
        }
        return breakPos == pos || secondPos == pos
    }

    private enum class SwitchMode : Displayable {
        NONE, LEGIT, GHOST, INVENTORY
    }
}