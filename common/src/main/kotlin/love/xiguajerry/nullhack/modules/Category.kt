package love.xiguajerry.nullhack.modules

import love.xiguajerry.nullhack.i18n.LocalizedNameable
import love.xiguajerry.nullhack.manager.managers.ModuleManager
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.IEnumEntriesProvider

sealed class Category(
    override val displayName: CharSequence
) : LocalizedNameable(ModuleManager.resolve(displayName.toString()), ModuleManager.i18N, displayName.toString()), Displayable {
    data object COMBAT : Category("Fight")
    data object PLAYER : Category("Player")
    data object MOVEMENT : Category("Movement")
    data object VISUAL : Category("Visual")
    data object MISC : Category("Miscellaneous")
    data object CLIENT : Category("Client")
    data object SCRIPT : Category("Script")
    data object HUD : Category("Hud")

    companion object : IEnumEntriesProvider<Category> {
        override val entries get() = listOf(COMBAT, PLAYER, MOVEMENT, VISUAL, MISC, CLIENT, SCRIPT, HUD)
    }
}