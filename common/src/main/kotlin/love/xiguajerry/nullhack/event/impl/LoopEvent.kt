package love.xiguajerry.nullhack.event.impl

import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting
import love.xiguajerry.nullhack.event.api.NamedProfilerEventBus

sealed class LoopEvent : IEvent {
    internal object Start : LoopEvent(), IPosting by NamedProfilerEventBus("start")
    internal object Tick : LoopEvent(), IPosting by NamedProfilerEventBus("tick")
    internal object Render : LoopEvent(), IPosting by NamedProfilerEventBus("render")
    internal object RenderPost : LoopEvent(), IPosting by NamedProfilerEventBus("renderPost")
    internal object End : LoopEvent(), IPosting by NamedProfilerEventBus("end")
}