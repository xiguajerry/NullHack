package love.xiguajerry.nullhack.event.impl.render

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting
import net.minecraft.client.gui.GuiGraphics

class Render2DEvent(val context: GuiGraphics, val ticksDelta: Float) : IEvent, IPosting by Companion {
    companion object : EventBus()
}