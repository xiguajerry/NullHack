package love.xiguajerry.nullhack.utils

import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

object ChatUtils {
    private const val SECTION_SIGN = '\u00A7'

    val BLACK = SECTION_SIGN + "0"
    val DARK_BLUE = SECTION_SIGN + "1"
    val DARK_GREEN = SECTION_SIGN + "2"
    val DARK_AQUA = SECTION_SIGN + "3"
    val DARK_RED = SECTION_SIGN + "4"
    val DARK_PURPLE = SECTION_SIGN + "5"
    val GOLD = SECTION_SIGN + "6"
    val GRAY = SECTION_SIGN + "7"
    val DARK_GRAY = SECTION_SIGN + "8"
    val BLUE = SECTION_SIGN + "9"
    val GREEN = SECTION_SIGN + "a"
    val AQUA = SECTION_SIGN + "b"
    val RED = SECTION_SIGN + "c"
    val LIGHT_PURPLE = SECTION_SIGN + "d"
    val YELLOW = SECTION_SIGN + "e"
    val WHITE = SECTION_SIGN + "f"
    val OBFUSCATED = SECTION_SIGN + "k"
    val BOLD = SECTION_SIGN + "l"
    val STRIKE_THROUGH = SECTION_SIGN + "m"
    val UNDER_LINE = SECTION_SIGN + "n"
    val ITALIC = SECTION_SIGN + "o"
    val RESET = SECTION_SIGN + "r"

    private val messagePrefix =
        ChatFormatting.WHITE.toString() + "[" + ChatFormatting.AQUA + NullHackMod.NAME + ChatFormatting.WHITE + "]" + ChatFormatting.RESET

    fun sendMessage(message: Any?) {
        sendMessage(message.toString())
    }

    fun sendMessage(message: String) {
        runSafe {
            player.displayClientMessage(
                Component.literal(translateAlternateColorCodes("$messagePrefix $message")),
                false
            )
        }
    }

    fun sendRawMessage(message: String) {
        runSafe {
            player.displayClientMessage(Component.literal(message), false)
        }
    }

    private fun translateAlternateColorCodes(textToTranslate: String): String {
        val b = textToTranslate.toCharArray()
        for (i in 0..<b.size - 1) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = SECTION_SIGN
                b[i + 1] = b[i + 1].lowercaseChar()
            }
        }
        return b.concatToString()
    }
}