package love.xiguajerry.nullhack.event.impl.player

import love.xiguajerry.nullhack.event.api.Cancellable
import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting
import net.minecraft.world.phys.Vec3

class PlayerUpdateVelocityEvent(
    var movementInput: Vec3, var speed: Float, var yaw: Float, var velocity: Vec3
) : Cancellable(), IEvent, IPosting by Companion {
    companion object : EventBus()
}