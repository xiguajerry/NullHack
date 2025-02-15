package love.xiguajerry.nullhack.manager.managers

import kotlinx.coroutines.runBlocking
import love.xiguajerry.nullhack.NullHackMod.i18N
import love.xiguajerry.nullhack.NullHackMod.resolve
import love.xiguajerry.nullhack.config.Configurable
import love.xiguajerry.nullhack.i18n.ILocalizedNameable
import love.xiguajerry.nullhack.i18n.LocalizedNameable
import love.xiguajerry.nullhack.manager.AbstractManager
import love.xiguajerry.nullhack.utils.ChatUtils
import love.xiguajerry.nullhack.config.ConfigCategories

object FriendManager : AbstractManager(), ILocalizedNameable by LocalizedNameable(resolve("friends"), i18N),
    Configurable by Configurable.NamedConfigurable("Friends", ConfigCategories.FRIENDS) {
    var friends by setting("friend_names", listOf()); private set

    fun isFriend(name: CharSequence) = name.toString() in friends

    fun clear() {
        friends = emptyList()
        runBlocking {
            ConfigManager.save()
        }
    }

    fun add(name: CharSequence) {
        ChatUtils.sendMessage("Added ${ChatUtils.GREEN}$name${ChatUtils.RESET} to friend.")
        friends += (name.toString())
        runBlocking {
            ConfigManager.save()
        }
    }

    fun remove(name: CharSequence) {
        ChatUtils.sendMessage("Removed ${ChatUtils.RED}$name{ChatUtils.RESET} from friends.")
        friends -= (name.toString())
        runBlocking {
            ConfigManager.save()
        }
    }
}