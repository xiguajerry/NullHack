package love.xiguajerry.nullhack.event.impl.world

import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting
import love.xiguajerry.nullhack.utils.world.explosion.advanced.CrystalDamage
import net.minecraft.world.entity.boss.enderdragon.EndCrystal

class CrystalSpawnEvent(
    val entityID: Int,
    val crystalDamage: CrystalDamage
) : IEvent, IPosting by Companion {
    companion object : EventBus()
}

class CrystalSetDeadEvent(
    val x: Double,
    val y: Double,
    val z: Double,
    val crystals: List<EndCrystal>
) : IEvent, IPosting by Companion {
    companion object : EventBus()
}