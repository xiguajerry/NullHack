package love.xiguajerry.nullhack.modules.impl.visual

import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import java.awt.Color

object FullBright : Module("Full Bright", "Maxes out the brightness.",category = Category.VISUAL) {
     val color by setting("Color", ColorRGBA.WHITE)
     val rgb get() = Color(color.r, color.g, color.b, color.a)
}