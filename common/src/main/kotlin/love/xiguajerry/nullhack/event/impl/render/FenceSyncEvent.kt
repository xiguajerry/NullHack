package love.xiguajerry.nullhack.event.impl.render

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting

object FenceSyncEvent : IEvent, IPosting by EventBus()