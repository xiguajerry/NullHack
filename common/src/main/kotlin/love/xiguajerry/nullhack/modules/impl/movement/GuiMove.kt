package love.xiguajerry.nullhack.modules.impl.movement

import com.mojang.blaze3d.platform.InputConstants
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.UpdateEvent
import love.xiguajerry.nullhack.event.impl.render.Render3DEvent
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.MinecraftWrapper.mc
import net.minecraft.client.gui.screens.ChatScreen

object GuiMove : Module("Gui Move", "Walk in inventory.", Category.MOVEMENT) {
    val sneak by setting("Sneak", true)

    init {
        nonNullHandler<Render3DEvent> {
            update()
        }

        nonNullHandler<UpdateEvent> {
            update()
        }
    }

    fun update() {
        if (mc.screen != null) {
            if (mc.screen !is ChatScreen) {
                for (k in arrayOf(
                    mc.options.keyDown,
                    mc.options.keyLeft,
                    mc.options.keyRight,
                    mc.options.keyJump,
                    mc.options.keySprint
                )) {
                    k.isDown = InputConstants.isKeyDown(
                        mc.window.window,
                        InputConstants.getKey(k.saveString()).value
                    )
                }

                mc.options.keyUp.isDown = InputConstants.isKeyDown(
                    mc.window.window,
                    InputConstants.getKey(mc.options.keyUp.saveString()).value
                )

                if (sneak) {
                    mc.options.keyShift.isDown = InputConstants.isKeyDown(
                        mc.window.window,
                        InputConstants.getKey(mc.options.keyShift.saveString()).value
                    )
                }
            }
        }
    }
}
