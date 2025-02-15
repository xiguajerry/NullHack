package love.xiguajerry.nullhack.event.impl.render

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting

class ResolutionUpdateEvent(val framebufferWidth: Int, val framebufferHeight: Int) : IEvent, IPosting by Companion {
    companion object : EventBus()
}