package love.xiguajerry.nullhack.utils.extension

import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import java.awt.Color

fun Color.injectAlpha(alpha: Int): ColorRGBA = ColorRGBA(this.red,this.green,this.blue,alpha)