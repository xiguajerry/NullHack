package love.xiguajerry.nullhack.modules.impl.player

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.PacketEvent
import love.xiguajerry.nullhack.mixins.accessor.IPositionMoveRotationAccessor
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.extension.pitch
import love.xiguajerry.nullhack.utils.extension.yaw
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket

object NoRotate : Module("No Rotate", "No Rotate", Category.PLAYER) {
    init {
        nonNullHandler<PacketEvent.Receive> {
            if (it.packet is ClientboundPlayerPositionPacket) {
                val yaw = player.yaw
                val pitch = player.pitch
                (it.packet.change as IPositionMoveRotationAccessor).setYRot(yaw)
                (it.packet.change as IPositionMoveRotationAccessor).setXRot(pitch)
            }
        }
    }
}