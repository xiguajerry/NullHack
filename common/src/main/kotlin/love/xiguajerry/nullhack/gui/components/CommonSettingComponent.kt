package love.xiguajerry.nullhack.gui.components

import love.xiguajerry.nullhack.RenderSystem
import love.xiguajerry.nullhack.config.settings.AbstractSetting
import love.xiguajerry.nullhack.config.settings.DoubleSetting
import love.xiguajerry.nullhack.config.settings.EnumSetting
import love.xiguajerry.nullhack.config.settings.FloatSetting
import love.xiguajerry.nullhack.gui.Component
import love.xiguajerry.nullhack.gui.NullHackGui
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings
import love.xiguajerry.nullhack.modules.impl.client.Colors
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.MinecraftWrapper.mc
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.font.TextComponent
import love.xiguajerry.nullhack.graphics.matrix.MatrixLayerStack
import love.xiguajerry.nullhack.graphics.matrix.scope
import love.xiguajerry.nullhack.graphics.matrix.translatef
import love.xiguajerry.nullhack.graphics.shader.BlurRenderer
import love.xiguajerry.nullhack.utils.math.MathUtils
import love.xiguajerry.nullhack.utils.math.vectors.HAlign
import love.xiguajerry.nullhack.utils.math.vectors.VAlign
import kotlin.math.absoluteValue
import kotlin.math.max
import love.xiguajerry.nullhack.RenderSystem as RS

open class CommonSettingComponent<V : Any, G : NullHackGui<G>>(
    x: Float, y: Float, override var width0: Float,
    open val setting: AbstractSetting<V, *>, override val father: ModuleComponent<G>
) : Component<ModuleComponent<G>, Nothing>(x, y, father) {
    override var height0: Float = BASE_HEIGHT
    override val width: Float
        get() = animationWidth.getAndUpdate(if (setting.isVisible && father.extended) width0 else 0f)
    override val height: Float
        get() = animationHeight.getAndUpdate(height0)
    override val children: MutableList<Nothing> = mutableListOf()
    val index get() = father.children.filter { it.setting.isVisible }.indexOf(this).takeIf { setting.isVisible } ?: 0
//    val yStep get() = father.y + BASE_HEIGHT + father.children
//        .filter { it.setting.isVisible }.subList(0, index).map { it.height }.sum()

    init {
        reset()
    }

    override fun render(mouseX: Int, mouseY: Int) {
        // if parent is the last module in the panel, then we should check how much the panel wraps the module component
        val moduleHeight = father.height
        val panelHeight = father.father.height
        val panelY = father.father.y
        if (father.index == father.father.children.size - 1) {
            if ((panelY + panelHeight - father.y).absoluteValue <= 0.1f) return
        } else // otherwise, check the height of module directly
            if ((moduleHeight - BASE_HEIGHT).absoluteValue <= 0.1f && !father.extended) return
        if (!setting.isVisible) {
            animationWidth.getAndUpdate(0f)
            return
        }
        val alpha = father.father.children.getOrNull(father.index + 1)?.let { nextModule ->
            if (y + height > nextModule.y) (1 - ((y + height - nextModule.y) / height).coerceIn(0f, 1f)) * 255f
            else 255f
        } ?: if (y + height < panelY + panelHeight) 255f
        else {
            (max(0f, 255f - (((y + height) - (panelY + panelHeight)) / height).coerceIn(0f, 1f) * 255f)).coerceIn(0f, 255f)
        }
        if (alpha.absoluteValue <= 5f) return
        RS.matrixLayer.scope {
            translatef(x, y, 0f)
            drawValue(mouseX, mouseY, alpha.toInt())
            val titleY = (BASE_HEIGHT - UnicodeFontManager.CURRENT_FONT.height) / 2
            UnicodeFontManager.CURRENT_FONT.drawText(setting.localizedName, MINOR_X_PADDING,
                titleY, ColorRGBA.WHITE.alpha(alpha.toInt()))
        }
    }

    context(MatrixLayerStack.MatrixScope)
    override fun drawTooltip(mouseX: Int, mouseY: Int): Boolean {
        if (father.extended && isHovered(mouseX, mouseY)) {
            drawInfo()
            return true
        }
        return false
    }

    open fun drawValue(mouseX: Int, mouseY: Int, fatherAlpha: Int) {
        val str = when (setting) {
            is EnumSetting<*> -> (setting.value as Displayable).displayString
            is FloatSetting -> MathUtils.round(setting.value as Float, MathUtils.decimalPlaces(setting.value as Float)).toString()
            is DoubleSetting -> MathUtils.round(setting.value as Double, MathUtils.decimalPlaces(setting.value as Double)).toString()
            else -> setting.value.toString()
        }
        val x = max(width - UnicodeFontManager.CURRENT_FONT.getWidth(str) - MINOR_X_PADDING, 0f)
        val y = (BASE_HEIGHT - UnicodeFontManager.CURRENT_FONT.height) / 2
        UnicodeFontManager.CURRENT_FONT.drawText(str, x, y, ColorRGBA.WHITE.alpha(fatherAlpha))
    }

    context(MatrixLayerStack.MatrixScope)
    open fun drawInfo() {
        if (mc.screen != this.father.father.father) return

        val mouseX = mc.mouseHandler.xpos() * RenderSystem.scaledWidth / RenderSystem.widthD
        val mouseY = mc.mouseHandler.ypos() * RenderSystem.scaledHeight / RenderSystem.heightD

        val info = TextComponent()
        info.addLine("Name: ${setting.localizedName}")
        val value = when (setting) {
            is EnumSetting<*> -> (setting.value as Displayable).displayString
            is FloatSetting -> MathUtils.round(setting.value as Float, MathUtils.decimalPlaces(setting.value as Float)).toString()
            is DoubleSetting -> MathUtils.round(setting.value as Double, MathUtils.decimalPlaces(setting.value as Double)).toString()
            else -> setting.value.toString()
        }
        info.addLine("Value: $value")
        if (setting.description != "") info.addLine("Description: ${setting.description}")

        var startX = mouseX - 4.0
        val startY = mouseY
        val vAlign = if (startY + info.getHeight(1) > RenderSystem.scaledHeight) {
            VAlign.BOTTOM
        } else VAlign.TOP
        val hAlign = if (startX + info.getWidth() > RenderSystem.scaledWidth) HAlign.RIGHT else {
            startX += 10.0
            HAlign.LEFT
        }

        translatef(startX.toFloat(), startY.toFloat(), 100f)
        val rectWidth = info.getWidth()
        val rectHeight = info.getHeight(1)
        val rectX = if (hAlign == HAlign.LEFT) 0f else -rectWidth
        val rectY = if (vAlign == VAlign.TOP) 0f else -rectHeight
        if (Colors.blur) BlurRenderer.render(
            rectX,
            rectY,
            rectX + rectWidth,
            rectY + rectHeight,
            ClientSettings.windowBlurPass
        )
        Render2DUtils.drawRect(
            rectX,
            rectY,
            rectX + rectWidth,
            rectY + rectHeight,
            ColorRGBA(GuiManager.background)
        )
        info.draw(lineSpace = 1, verticalAlign = vAlign, horizontalAlign = hAlign)
    }

    override fun clicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        return false
    }
}