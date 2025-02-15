package love.xiguajerry.nullhack.modules.impl.combat

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.PacketEvent
import love.xiguajerry.nullhack.event.impl.world.ConnectionEvent
import love.xiguajerry.nullhack.event.impl.world.WorldEvent
import love.xiguajerry.nullhack.mixins.accessor.IPlayerMoveC2SPacketAccessor
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.NonNullContext
import love.xiguajerry.nullhack.utils.math.ceilToInt
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.item.Items
import net.minecraft.world.item.MaceItem
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.phys.Vec3
import kotlin.math.abs


object MaceSpoof : Module("Mace Spoof", category = Category.COMBAT) {
    private val mode by setting("Mode", Mode.VANILLA)
    private val fallDistance by setting("Fall Distance", 10, 0..340, 5, description = "Heights > 10blocks only works on Paper/Spigot or integrated servers")
    private val noBlocking by setting("Cancel Blocking", true)

    private var prevPos = Vec3.ZERO

    init {
        nonNullHandler<ConnectionEvent.Connect> {
            prevPos = player.position()
        }

        nonNullHandler<WorldEvent.Load> {
            prevPos = player.position()
        }

        nonNullHandler<PacketEvent.Send>(priority = 114514) {
            val packet = it.packet
            if (packet is ServerboundInteractPacket && mode == Mode.VANILLA) {
                val interactType = packet.action.type
                if (interactType == ServerboundInteractPacket.ActionType.ATTACK
                    && player.mainHandItem.item == Items.MACE
                    && world.getEntity(packet.entityId) is LivingEntity) {
                    val targetEntity = world.getEntity(packet.entityId) as LivingEntity

                    if (noBlocking && ((targetEntity.isBlocking
                                && targetEntity.isDamageSourceBlocked(targetEntity.lastDamageSource!!))
                                || targetEntity.isInvulnerable || targetEntity.hasInfiniteMaterials())) return@nonNullHandler
                    prevPos = player.position()
                    val blocks = getMaxHeightAbovePlayer()

                    var packetsRequired = abs((blocks / 10).toDouble()).ceilToInt()

                    if (packetsRequired > 20) {
                        packetsRequired = 1
                    }

                    val isAir1 = (player.blockPosition().offset(0, blocks, 0))
                    val isAir2 = (player.blockPosition().offset(0, blocks + 1, 0))
                    if (isSafeBlock(isAir1) && isSafeBlock(isAir2)) {
                        if (player.isPassenger) {
                            for (packetNumber in 0 until (packetsRequired - 1)) {
                                netHandler.send(ServerboundMoveVehiclePacket.fromEntity(player.vehicle!!))
                            }
                            player.vehicle!!
                                .setPos(player.vehicle!!.x, player.vehicle!!.y + blocks, player.vehicle!!.z)
                            netHandler.send(ServerboundMoveVehiclePacket.fromEntity(player.vehicle!!))
                        } else {
                            for (packetNumber in 0 until (packetsRequired - 1)) {
                                netHandler.send(ServerboundMovePlayerPacket.StatusOnly(false, player.horizontalCollision))
                            }
                            netHandler.send(
                                ServerboundMovePlayerPacket.Pos(
                                    player.x, player.y + blocks, player.z, false,
                                    player.horizontalCollision
                                )
                            )
                        }

                        // Move back to original position
                        if (player.isPassenger) {
                            player.vehicle!!.setPos(prevPos)
                            netHandler.send(ServerboundMoveVehiclePacket.fromEntity(player.vehicle!!))
                            // Do it again to be sure it happens
                            player.vehicle!!.setPos(prevPos)
                            netHandler.send(ServerboundMoveVehiclePacket.fromEntity(player.vehicle!!))
                        } else {
                            netHandler.send(
                                ServerboundMovePlayerPacket.Pos(
                                    prevPos.x,
                                    prevPos.y,
                                    prevPos.z,
                                    false,
                                    player.horizontalCollision
                                )
                            )
                            // Do it again to be sure it happens
                            netHandler.send(
                                ServerboundMovePlayerPacket.Pos(
                                    prevPos.x,
                                    prevPos.y,
                                    prevPos.z,
                                    false,
                                    player.horizontalCollision
                                )
                            )
                        }
                    }
                }
            } else if (packet is ServerboundMovePlayerPacket && mode == Mode.SPOOF_GROUND) {
                (packet as IPlayerMoveC2SPacketAccessor).onGround = false
            }
        }
    }

    context(NonNullContext)
    private fun getMaxHeightAbovePlayer(): Int {
        val playerPos = player.blockPosition()
        val maxHeight = playerPos.y + fallDistance

        for (i in maxHeight downTo playerPos.y + 1) {
            val isAir1 = BlockPos(playerPos.x, i, playerPos.z)
            val isAir2 = isAir1.above(1)
            if (isSafeBlock(isAir1) && isSafeBlock(isAir2)) {
                return i - playerPos.y
            }
        }
        return 0 // Return 0 if no suitable position is found
    }

    context(NonNullContext)
    private fun isSafeBlock(pos: BlockPos): Boolean {
        return world.getBlockState(pos).canBeReplaced()
                && world.getFluidState(pos).isEmpty
                && !world.getBlockState(pos).`is`(Blocks.POWDER_SNOW)
    }

    // MaceItem.getBonusAttackDamage
    fun getBonusAttackDamage(target: Entity, baseAttackDamage: Float, damageSource: DamageSource): Float {
        val f = damageSource.causingEntity
        if (f is LivingEntity) {
            if (!MaceItem.canSmashAttack(f)) {
                return 0.0f
            } else {
                val h = f.fallDistance
                val i = if (h <= 3.0f) {
                    4.0f * h
                } else if (h <= 8.0f) {
                    12.0f + 2.0f * (h - 3.0f)
                } else {
                    22.0f + h - 8.0f
                }

                val var10 = f.level()
                return if (var10 is ServerLevel)
                    i + EnchantmentHelper.modifyFallBasedDamage(
                        var10,
                        f.weaponItem,
                        target,
                        damageSource,
                        0.0f
                    ) * h
                else
                    i
            }
        } else {
            return 0.0f
        }
    }

    private enum class Mode(override val displayName: CharSequence) : Displayable {
        VANILLA("Vanilla"), SPOOF_GROUND("Spoof Ground")
    }
}