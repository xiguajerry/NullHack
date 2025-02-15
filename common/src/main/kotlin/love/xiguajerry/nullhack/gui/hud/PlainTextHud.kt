package love.xiguajerry.nullhack.gui.hud

import love.xiguajerry.nullhack.gui.HudModule
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.modules.impl.client.Colors
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.font.TextComponent
import love.xiguajerry.nullhack.utils.math.vectors.HAlign
import love.xiguajerry.nullhack.utils.math.vectors.VAlign
import love.xiguajerry.nullhack.utils.math.vectors.Vec2d
import org.lwjgl.glfw.GLFW

abstract class PlainTextHud(
    name: CharSequence,
    description: CharSequence = "",
    hidden: Boolean = false,
    enableByDefault: Boolean = false,
    alwaysEnable: Boolean = false,
    defaultBind: Int = GLFW.GLFW_KEY_UNKNOWN,
    modulePriority: Int = 0,
    alias: Set<CharSequence> = setOf(name),
    internal: Boolean = false
) : HudModule(name, description, hidden, enableByDefault,
    alwaysEnable, defaultBind, modulePriority, alias, internal) {
    private var textWidth = 0f
    private var textHeight = 0f
    override val width: Float get() = textWidth
    override val height: Float get() = textHeight

    context (TextInfo)
    abstract fun TextComponent.buildText()

    override fun onRender2D(x: Float, y: Float) {
        val font = UnicodeFontManager.CURRENT_FONT
        val text = TextComponent(font = font)
        val info = TextInfo()
        with(info) {
            text.buildText()
        }
        textWidth = text.getWidth()
        textHeight = text.getHeight(info.lineSpace, info.skipEmptyLine)
        val prevUsingCache = !font.disableCache
        font.cache(info.cache)
        if (info.background)
            Render2DUtils.drawRect(x, y, x + width, y + height, ColorRGBA.BLACK.alpha(Colors.bAlpha))
        text.draw(Vec2d(x, y), info.lineSpace, info.alpha, info.scale,
            info.skipEmptyLine, info.horizontalAlign, info.verticalAlign, info.shadow)
        font.cache(prevUsingCache)
    }

    protected data class TextInfo(
        var lineSpace: Int = 0, var alpha: Float = 1f, var scale: Float = 1f,
        var skipEmptyLine: Boolean = true,
        var horizontalAlign: HAlign = HAlign.LEFT,
        var verticalAlign: VAlign = VAlign.TOP,
        var shadow: Boolean = true, var cache: Boolean = false, var background: Boolean = true
    )
}