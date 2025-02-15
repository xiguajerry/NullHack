package love.xiguajerry.nullhack.event.impl.player

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting

class HotbarUpdateEvent(val oldSlot: Int, val newSlot: Int) : IEvent, IPosting by Companion {
    companion object : EventBus()
}