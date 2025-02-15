package love.xiguajerry.nullhack.event.impl.player

import love.xiguajerry.nullhack.event.api.*
import love.xiguajerry.nullhack.utils.extension.velocityX
import love.xiguajerry.nullhack.utils.extension.velocityY
import love.xiguajerry.nullhack.utils.extension.velocityZ
import net.minecraft.client.player.LocalPlayer

sealed class PlayerMoveEvent : IEvent {
    class Pre(private val player: LocalPlayer) : PlayerMoveEvent(), ICancellable by Cancellable(),
        IPosting by Companion {
        private val prevX = player.velocityX
        private val prevY = player.velocityY
        private val prevZ = player.velocityZ

        val isModified: Boolean
            get() = x != prevX
                || y != prevY
                || z != prevZ

        var x = Double.NaN
            get() = get(field, player.velocityX)

        var y = Double.NaN
            get() = get(field, player.velocityY)

        var z = Double.NaN
            get() = get(field, player.velocityZ)

        private fun get(x: Double, y: Double): Double {
            return when {
                cancelled -> 0.0
                !x.isNaN() -> x
                else -> y
            }
        }

        companion object : NamedProfilerEventBus("nullPlayerMove")
    }

    object Post : PlayerMoveEvent(), IPosting by EventBus()
}