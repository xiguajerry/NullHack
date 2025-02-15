package love.xiguajerry.nullhack.event.impl.player

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting

class AirStrafingSpeedEvent(var speed: Float) : IEvent, IPosting by Companion {
    companion object : EventBus()
}