/*
 * Copyright (c) 2021-2022, SagiriXiguajerry. All rights reserved.
 * This repository will be transformed to SuperMic_233.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package love.xiguajerry.nullhack.manager.managers


import love.xiguajerry.nullhack.event.api.AlwaysListening
import love.xiguajerry.nullhack.gui.hud.impl.Notification
import love.xiguajerry.nullhack.utils.ChatUtils
import love.xiguajerry.nullhack.utils.runSafe
import net.minecraft.ChatFormatting

object NotificationManager : AlwaysListening {
    fun push(title: String, text: String, length: Long, type: Notification.NotificationType) {
        runSafe {
            if (Notification.isEnabled) {
                Notification.send(text, length)
            } else {
                ChatUtils.sendMessage("[$title] ${
                    when (type) {
                        Notification.NotificationType.INFO -> ChatFormatting.GREEN
                        Notification.NotificationType.WARN -> ChatFormatting.GOLD
                        Notification.NotificationType.ERROR -> ChatFormatting.RED
                    }
                }$text")
            }
        }
    }
}