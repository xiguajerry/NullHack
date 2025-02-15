package love.xiguajerry.nullhack.modules.impl.movement

import love.xiguajerry.nullhack.manager.managers.EntityMovementManager
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module

object SafeWalk: Module("Safe Walk","a safe walk",Category.MOVEMENT) {
    val eagle by setting("Eagle", false)

    init {
        onEnabled {
            if (!eagle) EntityMovementManager.isSafeWalk = true
        }

        onDisabled {
            if (!eagle) EntityMovementManager.isSafeWalk = false
        }
    }
}