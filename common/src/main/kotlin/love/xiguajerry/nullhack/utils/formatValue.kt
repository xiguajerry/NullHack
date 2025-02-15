package love.xiguajerry.nullhack.utils

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.TextColor

fun formatValue(value: String) = ChatFormatting.GRAY format "[$value]"

fun formatValue(value: Char) = ChatFormatting.GRAY format "[$value]"

fun formatValue(value: Any) = ChatFormatting.GRAY format "[$value]"

fun formatValue(value: Int) = ChatFormatting.GRAY format "($value)"

infix fun ChatFormatting.format(value: Any) = "$this$value${ChatFormatting.RESET}"

infix fun ChatFormatting.format(value: Int) = "$this$value${ChatFormatting.RESET}"

infix fun TextColor.format(value: Any) = "$this$value${ChatFormatting.RESET}"

//val ITextComponent.unformatted get() = TextChatFormatting.getTextWithoutChatFormattingCodes(this.unformattedText)!!

//val ITextComponent.unformattedComponent get() = TextChatFormatting.getTextWithoutChatFormattingCodes(this.unformattedComponentText)!!