package love.xiguajerry.nullhack.modules.impl.movement

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.LoopEvent
import love.xiguajerry.nullhack.event.impl.PacketEvent
import love.xiguajerry.nullhack.event.impl.player.PlayerPushOutOfBlockEvent
import love.xiguajerry.nullhack.mixins.accessor.IEntityVelocityUpdateS2CPacketAccessor
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.modules.impl.movement.Velocity.BypassMode.*
import love.xiguajerry.nullhack.utils.ChatUtils
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.Helper
import love.xiguajerry.nullhack.utils.timing.TickTimer
import love.xiguajerry.nullhack.utils.timing.TimeUnit
import net.minecraft.network.protocol.common.ClientboundPingPacket
import net.minecraft.network.protocol.common.ServerboundPongPacket
import net.minecraft.network.protocol.game.ClientboundExplodePacket
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.world.entity.Entity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

object Velocity : Module("Velocity", category = Category.MOVEMENT), Helper {
    private val mode by setting("Mode", NCP)
    private val waitTickGrim by setting("Wait Tick", 1, 1..20)
    private val horizontal by setting("Horizontal", 0.0f, -5.0f..5.0f, 0.05f)
    private val vertical by setting("Vertical", 0.0f, -5.0f..5.0f, 0.05f)
    private val waitTickFlag by setting("Flag Wait Tick", 30, 1..60, 1)
    private val minHurtTime by setting("Min HurtTime", 8, 0..10, 1)
    private val noPush = setting("No Push", true)
    private val entity by setting("Entity", true, noPush.visibility)
    private val liquid by setting("Liquid", true, noPush.visibility)
    private val block by setting("Block", true, noPush.visibility)
    private val pushable by setting("Pushable", true, noPush.visibility)
    private val debug by setting("Debug", true)

    private var grimPacket = false
    private var velocityPacketFinishOnHurtTime = false
    private var flag = false
    private val transactionQueue: Queue<Int> = ConcurrentLinkedQueue()
    private val needSeedPongQueue: Queue<Int> = ConcurrentLinkedQueue()
    private val timer = TickTimer(TimeUnit.TICKS)
    private val flagTimer = TickTimer(TimeUnit.TICKS)

    override fun getDisplayInfo(): Any {
        return "$horizontal/$vertical"
    }

    init {
        nonNullHandler<LoopEvent.Tick> {
            when (mode) {
                NCP -> {

                }
            }
        }

        nonNullHandler<PacketEvent.Receive>(-1000) {

            onDisabled {
                velocityPacketFinishOnHurtTime = false
                grimPacket = false
                flag = false
            }

            when (it.packet) {
                is ClientboundSetEntityMotionPacket -> {
                    when (mode) {
                        NCP -> {
                            with(it.packet) {
                                if (it.packet.id != player.id) return@nonNullHandler
                                if (isZero())
                                    it.cancel()
                                else {
                                    (it.packet as IEntityVelocityUpdateS2CPacketAccessor).apply {
                                        setX((xa * horizontal).toInt())
                                        setY((ya * vertical).toInt())
                                        setZ((za * horizontal).toInt())
                                    }
                                }
                            }
                        }
                    }
                }

                is ClientboundExplodePacket -> {
                    if (isZero())
                        it.cancel()
                    else {
                        (it.packet.center).apply {
                            netHandler.send(
                                ServerboundMovePlayerPacket.Pos(
                                    player.x,
                                    player.y + 0.0000000000000013,
                                    player.z,
                                    true,
                                    player.horizontalCollision
                                )
                            )
                            netHandler.send(
                                ServerboundMovePlayerPacket.Pos(
                                    player.x,
                                    player.y + 0.0000000000000027,
                                    player.z,
                                    false,
                                    player.horizontalCollision
                                )
                            )
                            x = it.packet.playerKnockback.get().x * this@Velocity.horizontal
                            y = it.packet.playerKnockback.get().y * this@Velocity.vertical
                            z = it.packet.playerKnockback.get().z * this@Velocity.horizontal
                        }
                    }
                }
            }

            if (it.packet is ClientboundSetEntityMotionPacket && it.packet.id == player.id) {
                if (player.hurtTime < minHurtTime) {
                    if (debug && !flag) ChatUtils.sendMessage("Detection grim flag, change to strict.")
                    flag = true

                    return@nonNullHandler
                }

                if (player.hurtTime > minHurtTime && !velocityPacketFinishOnHurtTime && !flag) {
                    it.cancel().let {
                        if (debug) ChatUtils.sendMessage("Detection self s2c velocity, cancel.")
                    }
                    velocityPacketFinishOnHurtTime = true
                    grimPacket = true

                    if (timer.tickAndReset(waitTickGrim)) {
                        velocityPacketFinishOnHurtTime = false
                    }
                }
            }

            if (it.packet is ClientboundPingPacket) {
                if (!grimPacket || transactionQueue.isEmpty()) return@nonNullHandler
                if (flag) return@nonNullHandler
                if (transactionQueue.remove(it.packet.id)) {
                    if (debug) ChatUtils.sendMessage("Successfully found packet(${it.packet.id}), cancel.")
                    it.cancel().let {
                        if (debug) ChatUtils.sendMessage("Detection self s2c velocity, cancel.")
                    }
                }
            }

        }

        nonNullHandler<PacketEvent.Send> {
            when (mode) {
                NCP -> {

                }
            }
        }

        nonNullHandler<PlayerPushOutOfBlockEvent.Push> {
            if (block) {
                it.cancel()
            }
        }
    }

    private fun isZero(): Boolean {
        return horizontal == 0.0f && vertical == 0.0f
    }

    @JvmStatic
    fun handleApplyEntityCollision(entity1: Entity, entity2: Entity, ci: CallbackInfo) {
        ci.cancel()
    }

    @JvmStatic
    fun shouldCancelLiquidVelocity(): Boolean {
        return isEnabled && noPush.value && liquid
    }

    @JvmStatic
    fun shouldCancelMove(): Boolean {
        return isEnabled && pushable
    }

    enum class BypassMode(override val displayName: CharSequence) : Displayable {
        NCP("NCP")
    }
}