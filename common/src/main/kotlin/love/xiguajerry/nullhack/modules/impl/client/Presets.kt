package love.xiguajerry.nullhack.modules.impl.client

import love.xiguajerry.nullhack.gui.hud.impl.Notification
import love.xiguajerry.nullhack.manager.managers.ConfigManager
import love.xiguajerry.nullhack.manager.managers.NotificationManager
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.runSafe

object Presets : Module("Presets", category = Category.CLIENT, excludedFromConfig = true) {
    var target by setting("Target", "default")
    private val save by setting("Save", false).register { _, _ ->
        runSafe {
            trySave()
        }
        false
    }
    private val load by setting("Load", false).register { _, _ ->
        runSafe {
            tryLoad()
        }
        false
    }

    fun trySave() {
        ConfigManager.namespace = target
        ConfigManager.save()
        NotificationManager.push("Config", "Saved current config to ${ConfigManager.namespace}",
            10000, Notification.NotificationType.INFO)
    }

    fun tryLoad() {
        ConfigManager.namespace = target
        val result = ConfigManager.tryLoad()
        if (result) NotificationManager.push("Config", "Loaded current config from ${ConfigManager.namespace}",
            10000, Notification.NotificationType.INFO)
        else NotificationManager.push("Config", "Failed to load current config from ${ConfigManager.namespace}",
            10000, Notification.NotificationType.WARN)
    }
}