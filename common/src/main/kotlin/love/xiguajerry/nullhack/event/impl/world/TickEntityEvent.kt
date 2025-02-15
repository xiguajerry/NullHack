package love.xiguajerry.nullhack.event.impl.world

import love.xiguajerry.nullhack.event.api.*
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.Entity

sealed class TickEntityEvent(val entity: Entity, val world: ClientLevel) : IEvent {
    class Pre(entity: Entity, world: ClientLevel) : TickEntityEvent(entity, world), IPosting by Companion, ICancellable by Cancellable() {
        companion object : EventBus()
    }

    class Post(entity: Entity, world: ClientLevel) : TickEntityEvent(entity, world), IPosting by Companion {
        companion object : EventBus()
    }
}