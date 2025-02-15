package love.xiguajerry.nullhack.modules.impl.client

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.render.Render2DEvent
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.graphics.color.ColorRGBA

object Watermark : Module("Watermark", category = Category.CLIENT) {
    private val color1 by setting("Color1", ColorRGBA.WHITE)
    private val color2 by setting("Color2", ColorRGBA.BLACK)

    init {
        nonNullHandler<Render2DEvent> {
            with (it.context) {
                UnicodeFontManager.GENSHIN_18.drawGradientTextWithShadow(
                    "原神", 10f, 10f, arrayOf(color1.awt, color2.awt))
            }
        }
    }
}