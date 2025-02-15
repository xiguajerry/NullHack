package love.xiguajerry.nullhack.event.impl.player

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting

sealed interface PlayerJumpEvent : IEvent, IPosting {
    data object Pre : EventBus(), PlayerJumpEvent
    data object Post : EventBus(), PlayerJumpEvent
}