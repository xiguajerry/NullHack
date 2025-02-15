package love.xiguajerry.nullhack.event.impl.world

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting
import net.minecraft.network.chat.Component

sealed class ConnectionEvent : IEvent {
    object Connect : ConnectionEvent(), IPosting by EventBus()
    class Disconnect(val message: Component) : ConnectionEvent(), IPosting by Companion {
        companion object : EventBus()
    }
}