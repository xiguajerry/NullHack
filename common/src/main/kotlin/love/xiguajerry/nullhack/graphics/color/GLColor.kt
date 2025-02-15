package love.xiguajerry.nullhack.graphics.color

data class GLColor(val r: Float, val g: Float, val b: Float, val a: Float) {
    fun toColorRGBA() = ColorRGBA(r, g, b, a)
}
