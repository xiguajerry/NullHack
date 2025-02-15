package love.xiguajerry.nullhack.graphics.font

import java.awt.Color

interface IGradientFontRenderer {
    fun drawGradientText(text: String, x: Float, y: Float, colors: Array<Color>)

    fun drawGradientText(text: String, x: Float, y: Float, colors: Array<Color>, scale: Float)

    fun drawGradientTextWithShadow(text: String, x: Float, y: Float, colors: Array<Color>)

    fun drawGradientTextWithShadow(text: String, x: Float, y: Float, colors: Array<Color>, scale: Float)

    fun drawGradientTextWithShadow(text: String, x: Float, y: Float, shadowDepth: Float, colors: Array<Color>)

    fun drawGradientTextWithShadow(
        text: String,
        x: Float,
        y: Float,
        shadowDepth: Float,
        colors: Array<Color>,
        scale: Float
    )
}