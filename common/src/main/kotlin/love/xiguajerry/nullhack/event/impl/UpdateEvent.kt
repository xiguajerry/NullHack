package love.xiguajerry.nullhack.event.impl

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting

object UpdateEvent : IEvent, IPosting by EventBus()