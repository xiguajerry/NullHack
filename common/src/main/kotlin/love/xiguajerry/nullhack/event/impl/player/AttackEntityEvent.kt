package love.xiguajerry.nullhack.event.impl.player

import love.xiguajerry.nullhack.event.api.*
import net.minecraft.world.entity.Entity

class AttackEntityEvent(val entity: Entity) : IEvent, ICancellable by Cancellable(), IPosting by AttackEntityEvent {
    companion object : EventBus()
}