package love.xiguajerry.nullhack.modules.impl.visual

import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module

object MotionBlur : Module("Motion Blur", category = Category.VISUAL) {
    val strength by setting("Strength", 3.0f)
}