package love.xiguajerry.nullhack.modules.impl.client

import love.xiguajerry.nullhack.gui.NullClickGui
import love.xiguajerry.nullhack.manager.managers.ModuleManager
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.runSafe

object ReloadScript : Module("Reload Script", category = Category.CLIENT) {
    init {
        onEnabled {
            runSafe {
                ModuleManager.modules.removeIf {
                    it.category == Category.SCRIPT
                }
                ModuleManager.loadScript()
            }
            NullClickGui.reloadPanel()
            disable()
        }
    }
}