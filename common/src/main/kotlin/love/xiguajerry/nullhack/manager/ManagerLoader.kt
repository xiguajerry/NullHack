package love.xiguajerry.nullhack.manager

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import love.xiguajerry.nullhack.command.CommandManager
import love.xiguajerry.nullhack.gui.NullClickGui
import love.xiguajerry.nullhack.gui.NullHudEditor
import love.xiguajerry.nullhack.manager.managers.*
import love.xiguajerry.nullhack.utils.Profiler
import love.xiguajerry.nullhack.graphics.font.UnicodeFontRenderer
import love.xiguajerry.nullhack.utils.threads.RenderThreadCoroutine

object ManagerLoader {
    private val managers = buildList {
        add { UnicodeFontManager }
        add { EntityManager }
        add { EntityMovementManager }
        add { ModuleManager }
        add { ConfigManager }
        add { FriendManager }
        add { CommandManager }
        add { GuiManager }
        add { HotbarSwitchManager }
        add { PlayerPacketManager }
        add { CombatManager }
        add { PlayerPopTotemManager }
    }

    /** may be called out of main thread, so delegate render operations to render thread. **/
    fun load() {
        managers.forEach {
            Profiler.BootstrapProfiler(it()::class.simpleName.toString()) {
                it().load(this)
            }
        }
        NullHudEditor.reloadPanel()
        NullClickGui.reloadPanel()
        runBlocking {
            RenderThreadCoroutine.async {
                Profiler.BootstrapProfiler("Render Utils") {
                    UnicodeFontRenderer.refresh()
                }
            }.await()
        }
    }
}