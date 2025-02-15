package love.xiguajerry.nullhack.event.impl.world

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting
import net.minecraft.world.entity.LivingEntity

sealed class CombatEvent : IEvent {
    abstract val entity: LivingEntity?

    class UpdateTarget(val prevEntity: LivingEntity?, override val entity: LivingEntity?) : CombatEvent(),
        IPosting by Companion {
        companion object : EventBus()
    }
}
