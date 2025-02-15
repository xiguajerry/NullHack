package love.xiguajerry.nullhack.event.impl.render

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting

class CoreRender3DEvent(val ticksDelta: Float) : IEvent, IPosting by Companion {
    companion object : EventBus()
}