package love.xiguajerry.nullhack.modules.impl.player

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.PacketEvent
import love.xiguajerry.nullhack.mixins.accessor.IPlayerMoveC2SPacketAccessor
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket

object AntiHunger : Module(
    "Anti Hunger",
    "Reduces (does NOT remove) hunger consumption.",
    Category.PLAYER
) {
    private val sprint by setting("sprint",true)
    private val onGround by setting("on-ground", true)

    init {
        nonNullHandler<PacketEvent.Send> {
            if (player.isPassenger || player.isInWater || player.isUnderWater) return@nonNullHandler
            if (it.packet is ServerboundPlayerCommandPacket && sprint) {
                if (it.packet.action == ServerboundPlayerCommandPacket.Action.START_SPRINTING) it.cancel()
            }
            if (it.packet is ServerboundMovePlayerPacket && onGround
                && player.onGround() && player.fallDistance <= 0.0 && !interaction.isDestroying) {
                (it.packet as IPlayerMoveC2SPacketAccessor).setOnGround(false)
            }
        }
    }
}