package love.xiguajerry.nullhack.event.impl.player

import love.xiguajerry.nullhack.event.api.*

sealed class PlayerPushOutOfBlockEvent : IEvent {
    class Push : PlayerPushOutOfBlockEvent(), ICancellable by Cancellable(), IPosting by Push {
        companion object : EventBus()
    }
}