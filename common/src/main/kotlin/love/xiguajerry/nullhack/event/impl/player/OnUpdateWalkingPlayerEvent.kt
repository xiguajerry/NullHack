package love.xiguajerry.nullhack.event.impl.player


import love.xiguajerry.nullhack.event.api.Cancellable
import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting
import love.xiguajerry.nullhack.manager.managers.PlayerPacketManager
import love.xiguajerry.nullhack.utils.math.vectors.Vec2f
import net.minecraft.world.phys.Vec3

sealed class OnUpdateWalkingPlayerEvent(
    position: Vec3,
    rotation: Vec2f,
    onGround: Boolean
) : Cancellable(), IEvent {

    var position = position; private set
    var rotation = rotation; private set

    var onGround = onGround
        @JvmName("isOnGround") get
        private set

    var cancelMove = false; private set
    var cancelRotate = false; private set
    var cancelAll = false; private set

    val rotationX: Float
        get() = rotation.x

    val rotationY: Float
        get() = rotation.y

    fun apply(packet: PlayerPacketManager.Packet) {
        cancel()

        packet.position?.let {
            this.position = it
        }
        packet.rotation?.let {
            this.rotation = it
        }
        packet.onGround?.let {
            this.onGround = it
        }

        this.cancelMove = packet.cancelMove
        this.cancelRotate = packet.cancelRotate
        this.cancelAll = packet.cancelAll
    }

    class Pre(position: Vec3, rotation: Vec2f, onGround: Boolean) :
        OnUpdateWalkingPlayerEvent(position, rotation, onGround), IPosting by Companion {
        constructor(position: Vec3, rotationX: Float, rotationY: Float, onGround: Boolean) : this(
            position,
            Vec2f(rotationX, rotationY),
            onGround
        )

        companion object : EventBus()
    }

    class Post(position: Vec3, rotation: Vec2f, onGround: Boolean) :
        OnUpdateWalkingPlayerEvent(position, rotation, onGround), IPosting by Companion {
        constructor(position: Vec3, rotationX: Float, rotationY: Float, onGround: Boolean) : this(
            position,
            Vec2f(rotationX, rotationY),
            onGround
        )

        companion object : EventBus()
    }
}