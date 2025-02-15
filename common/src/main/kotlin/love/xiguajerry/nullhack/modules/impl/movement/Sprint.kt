package love.xiguajerry.nullhack.modules.impl.movement


import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.TickEvent
import love.xiguajerry.nullhack.event.impl.player.OnUpdateWalkingPlayerEvent
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.extension.isMoving

object Sprint : Module(
    "Sprint",
    "Automatically makes the player sprint",
    Category.MOVEMENT
) {
     private val limit by setting("Limit", true)
    init {
        nonNullHandler<TickEvent.Pre> {
            if (limit) {
                mc.options.keySprint.isDown = true
            }
        }

        nonNullHandler<OnUpdateWalkingPlayerEvent.Pre> {
            if (!limit) {
                if (player.foodData.foodLevel <= 6) return@nonNullHandler
                player.isSprinting = player.isMoving() && !player.isShiftKeyDown
            }
        }
    }
}
