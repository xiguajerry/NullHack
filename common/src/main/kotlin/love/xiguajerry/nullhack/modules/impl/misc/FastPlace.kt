package love.xiguajerry.nullhack.modules.impl.misc

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.UpdateEvent
import love.xiguajerry.nullhack.mixins.accessor.IMinecraftClientAccessor
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module

object FastPlace : Module(
    "Fast Place",
    "Places blocks exceptionally fast",
    Category.MISC
) {

    init {
        nonNullHandler<UpdateEvent> {
            (mc as IMinecraftClientAccessor ).setRightClickDelay(0)
        }
    }
}