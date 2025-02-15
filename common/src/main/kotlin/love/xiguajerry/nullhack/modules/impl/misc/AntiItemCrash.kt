package love.xiguajerry.nullhack.modules.impl.misc

import love.xiguajerry.nullhack.event.api.handler
import love.xiguajerry.nullhack.event.impl.render.RenderEntityEvent
import love.xiguajerry.nullhack.event.impl.world.TickEntityEvent
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import net.minecraft.world.entity.item.ItemEntity

object AntiItemCrash : Module("Anti Item Crash", category = Category.MISC) {
    init {
        handler<RenderEntityEvent.All.Pre> { event ->
            if (event.entity is ItemEntity) event.cancel()
        }

        handler<TickEntityEvent.Pre> { event ->
            if (event.entity is ItemEntity) event.cancel()
        }
    }
}