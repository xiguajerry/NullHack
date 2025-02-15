package love.xiguajerry.nullhack.gui.hud.impl

import love.xiguajerry.nullhack.gui.hud.PlainTextHud
import love.xiguajerry.nullhack.utils.MinecraftWrapper
import love.xiguajerry.nullhack.graphics.font.TextComponent

object HudFPS : PlainTextHud("FPS Hud") {
    context (TextInfo)
    override fun TextComponent.buildText() {
        addLine("FPS: ${MinecraftWrapper.mc.fps}")
    }
}