package love.xiguajerry.nullhack.utils.extension

import love.xiguajerry.nullhack.manager.managers.PlayerPacketManager
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings
import love.xiguajerry.nullhack.utils.math.vectors.Vec2f
import love.xiguajerry.nullhack.utils.math.vectors.VectorUtils.toViewVec
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

/**
 * Check if a box is in sight
 */
fun AABB.isInSight(
    posFrom: Vec3 = PlayerPacketManager.position,
    rotation: Vec2f = PlayerPacketManager.rotation,
    range: Double = 8.0
): Boolean {
    return isInSight(posFrom, rotation.toViewVec(), range)
}

/**
 * Check if a box is in sight
 */
fun AABB.isInSight(
    posFrom: Vec3,
    viewVec: Vec3,
    range: Double = 4.25
): Boolean {
    val sightEnd = posFrom.add(viewVec.scale(range))
    return this.inflate(ClientSettings.placeRotationBoundingBoxGrow).intersects(posFrom, sightEnd)
}