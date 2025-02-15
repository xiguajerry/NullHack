package love.xiguajerry.nullhack.modules.impl.movement

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.UpdateEvent
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.extension.velocityY
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.world.item.Items

object NoFall : Module(
    "No Fall",
    "Prevents fall damage.",
    Category.MOVEMENT
) {
    init {
        nonNullHandler<UpdateEvent> {
            if (!isHoldingMace(player) && isFallingFastEnoughToCauseDamage(player)) {
                netHandler.send(ServerboundMovePlayerPacket.StatusOnly(true, player.horizontalCollision))
            }
        }
    }

    private fun isHoldingMace(player: LocalPlayer): Boolean {
        return player.mainHandItem.`is`(Items.MACE)
    }

    private fun isFallingFastEnoughToCauseDamage(player: LocalPlayer): Boolean {
        return player.velocityY < -0.5
    }
}