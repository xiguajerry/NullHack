package love.xiguajerry.nullhack.utils.combat

import dev.fastmc.common.toDegree
import dev.fastmc.common.toRadians
import love.xiguajerry.nullhack.utils.NonNullContext
import love.xiguajerry.nullhack.utils.extension.prevYaw
import love.xiguajerry.nullhack.utils.extension.yaw
import love.xiguajerry.nullhack.utils.math.RotationUtils
import net.minecraft.client.player.ClientInput
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.player.Input
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object MovementUtils {
    context(NonNullContext)
    fun isInputting(
        movementInput: ClientInput = player.input,
        jump: Boolean = false,
        sneak: Boolean = false
    ): Boolean {
        return movementInput.forwardImpulse != 0.0f
                || movementInput.leftImpulse != 0.0f
                || jump && movementInput.keyPresses.jump
                || sneak && movementInput.keyPresses.shift
    }

    /* totally not taken from elytrafly */
    fun LocalPlayer.calcMoveYaw(): Double {
        return calcMoveYaw(
            yaw = yaw,
            moveForward = input.forwardImpulse,
            moveStrafe = input.leftImpulse
        )
    }

    fun calcMoveYaw(
        yaw: Float,
        moveForward: Float,
        moveStrafe: Float
    ): Double {
        val moveYaw = if (moveForward == 0.0f && moveStrafe == 0.0f) 0.0
        else atan2(moveForward, moveStrafe).toDegree() - 90.0
        return RotationUtils.normalizeAngle(yaw + moveYaw).toRadians()
    }

    context(NonNullContext)
    fun directionSpeed(speed: Double): DoubleArray {
        var forward: Float = player.input.forwardImpulse
        var side: Float = player.input.leftImpulse
        var yaw: Float = player.prevYaw + (player.yaw - player.prevYaw) * mc.deltaTracker.getGameTimeDeltaPartialTick(false)
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (if ((forward > 0.0f)) -45 else 45).toFloat()
            } else if (side < 0.0f) {
                yaw += (if ((forward > 0.0f)) 45 else -45).toFloat()
            }
            side = 0.0f
            if (forward > 0.0f) {
                forward = 1.0f
            } else if (forward < 0.0f) {
                forward = -1.0f
            }
        }
        val sin = sin(Math.toRadians((yaw + 90.0f).toDouble()))
        val cos = cos(Math.toRadians((yaw + 90.0f).toDouble()))
        val posX = forward * speed * cos + side * speed * sin
        val posZ = forward * speed * sin - side * speed * cos
        return doubleArrayOf(posX, posZ)
    }
    
}