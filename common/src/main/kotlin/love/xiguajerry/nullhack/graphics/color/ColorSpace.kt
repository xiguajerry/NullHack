package love.xiguajerry.nullhack.graphics.color

sealed interface ColorSpace {
    interface ARGB : ColorSpace
    interface RGBA : ColorSpace
}