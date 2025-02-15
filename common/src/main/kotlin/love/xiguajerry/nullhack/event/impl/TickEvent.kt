package love.xiguajerry.nullhack.event.impl

import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting
import love.xiguajerry.nullhack.event.api.NamedProfilerEventBus

sealed class TickEvent : IEvent {
    internal object Pre : TickEvent(), IPosting by NamedProfilerEventBus("NullTickPre")
    internal object Post : TickEvent(), IPosting by NamedProfilerEventBus("NullTickPost")
}