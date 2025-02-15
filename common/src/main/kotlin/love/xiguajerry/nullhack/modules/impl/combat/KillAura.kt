package love.xiguajerry.nullhack.modules.impl.combat

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.TickEvent
import love.xiguajerry.nullhack.event.impl.player.OnUpdateWalkingPlayerEvent
import love.xiguajerry.nullhack.manager.managers.EntityManager
import love.xiguajerry.nullhack.manager.managers.PlayerPacketManager.sendPlayerPacket
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.NonNullContext
import love.xiguajerry.nullhack.utils.delegates.CachedValueN
import love.xiguajerry.nullhack.utils.extension.realHealth
import love.xiguajerry.nullhack.utils.math.RotationUtils
import love.xiguajerry.nullhack.utils.math.sq
import love.xiguajerry.nullhack.utils.runSafe
import love.xiguajerry.nullhack.utils.timing.TickTimer
import love.xiguajerry.nullhack.utils.world.EntityUtils
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.SwordItem
import net.minecraft.world.phys.Vec3
import kotlin.math.max

object KillAura : Module("Kill Aura", category = Category.COMBAT) {
    private val range by setting("Range", 6.0f, 0.1f..7.0f)
    private val players by setting("Players", true)
    private val mobs by setting("Mobs", true)
    private val passive by setting("Passive", true)
    private val neutral by setting("Neutral", true)
    private val hostile by setting("Hostile", true)
    private val cooldown by setting("Cooldown", 1.1f, 0f..1.2f, 0.1f)
    private val rotate by setting("Rotation", true)
    private val switchMode by setting("Target", Switching.DISTANCE)
    private val swordOnly by setting("Sword Only", true)
    private val eatingPause by setting("Pause While Eating", true)

    private var target: Entity? by CachedValueN(100L) {
        runSafe {
            EntityManager.entity.filter {
                it != player && it is LivingEntity && it.realHealth >= 0.0 && !it.isDeadOrDying &&
                        it.distanceToSqr(player) < range.sq &&
                        if (it is Player) players && EntityUtils.playerTypeCheck(it, true, false)
                        else EntityUtils.mobTypeSettings(it, mobs, passive, neutral, hostile)
            }.minByOrNull { switchMode.chooser(this@runSafe, it) }
        }
    }

    private val tick = TickTimer()
    private var attackTicks = 0
    private var directionVec = Vec3.ZERO

    init {
        nonNullHandler<OnUpdateWalkingPlayerEvent.Pre> {
            if (swordOnly && player.mainHandItem.item !is SwordItem) {
                return@nonNullHandler
            }
            if (rotate) {
                val target = target ?: return@nonNullHandler
                sendPlayerPacket {
                    rotate(RotationUtils.getRotationTo(target.eyePosition))
                }
            }
        }

        nonNullHandler<TickEvent.Post> {
            if (tick.tick(50)) {
                attackTicks++
                tick.reset()
            }
            if (swordOnly && player.mainHandItem.item !is SwordItem) {
                return@nonNullHandler
            }
            if (check()) {
                doAura()
            }
        }
    }

    context(NonNullContext)
    private fun doAura() {
        if (!check()) {
            return
        }
        val target = target ?: return

        netHandler.send(
            ServerboundInteractPacket.createAttackPacket(
                target, player.isShiftKeyDown
            )
        )

        player.resetAttackStrengthTicker()
        player.swing(InteractionHand.MAIN_HAND)
        attackTicks = 0
    }


    context(NonNullContext)
    private fun check(): Boolean {
        val at = player.attackStrengthTicker
//        at = ((PlayerPacketManager.clientTps.toInt() / 20f) * at).toInt()
        if (!(max(
                (at / player.currentItemAttackStrengthDelay).toDouble(),
                0.0
            ) >= cooldown)
        ) return false
        return !eatingPause || !player.isUsingItem
    }

    private enum class Switching(val chooser: NonNullContext.(Entity) -> Double) : Displayable {
        DISTANCE({ it.distanceToSqr(player) }), HEALTH({ (it as? LivingEntity)?.realHealth?.toDouble() ?: 0.0 })
    }
}