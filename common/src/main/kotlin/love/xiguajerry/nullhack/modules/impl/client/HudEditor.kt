package love.xiguajerry.nullhack.modules.impl.client

import love.xiguajerry.nullhack.event.api.handler
import love.xiguajerry.nullhack.event.impl.world.WorldEvent
import love.xiguajerry.nullhack.gui.NullHudEditor
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

object HudEditor : Module("Hud Editor", category = Category.CLIENT, defaultBind = GLFW.GLFW_KEY_UNKNOWN) {
    val pauseGame by setting("Pause Game", false)
    private val reloadOnEnabled by setting("Reload", false)
    val outline by setting("Outline", true)
    val mouseScrollSpeed by setting("Mouse Scroll Speed", 10, 1..20)
    val fix by setting("Fix Gui", false)

    private val guiScreen get() = NullHudEditor

    init {
        handler<WorldEvent.Load> {
            disable()
        }

        onEnabled {
            try {
                ClickGui.disable()
                if (reloadOnEnabled) guiScreen.reloadPanel()
                Minecraft.getInstance().setScreen(guiScreen)
            } catch (_: Exception) { }
        }

        onDisabled {
//            MinecraftClient.getInstance().setScreenAndRender(null)
//            Coroutine.launch {
//                ConfigManager.save()
//            }
        }
    }
}