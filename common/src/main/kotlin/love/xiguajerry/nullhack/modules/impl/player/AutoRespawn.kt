package love.xiguajerry.nullhack.modules.impl.player

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.UpdateEvent
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import net.minecraft.client.gui.screens.DeathScreen

object AutoRespawn : Module(
    "Auto Respawn", "Automatically respawns when you die.", Category.PLAYER
) {
    init {
        nonNullHandler<UpdateEvent> {
            if (mc.screen is DeathScreen) {
                player.respawn()
                mc.setScreen(null)
            }
        }
    }
}