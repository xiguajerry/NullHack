package love.xiguajerry.nullhack.gui.components

import love.xiguajerry.nullhack.RenderSystem
import love.xiguajerry.nullhack.config.settings.DoubleSetting
import love.xiguajerry.nullhack.config.settings.EnumSetting
import love.xiguajerry.nullhack.config.settings.FloatSetting
import love.xiguajerry.nullhack.config.settings.LabelSetting
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

class LabelSettingComponent<G : NullHackGui<G>>(
    x: Float, y: Float, width0: Float,
    setting: LabelSetting, father: ModuleComponent<G>
) : CommonSettingComponent<Unit, G>(x, y, width0, setting, father) {
    override fun render(mouseX: Int, mouseY: Int) {
        // if parent is the last module in the panel, then we should check how much the panel wraps the module component
        if (father.index == father.father.children.size - 1) {
            if ((father.father.y + father.father.height - father.y).absoluteValue <= 0.1f) return
        } else if (father.index != father.father.children.size - 1) // otherwise, check the height of module directly
            if ((father.height - BASE_HEIGHT).absoluteValue <= 0.1f && !father.extended) return
        if (!setting.isVisible) {
            animationWidth.getAndUpdate(0f)
            return
        }
        val alpha = father.father.children.getOrNull(father.index + 1)?.let { nextModule ->
            if (y + height > nextModule.y) (1 - ((y + height - nextModule.y) / height).coerceIn(0f, 1f)) * 255f
            else 255f
        } ?: if (y + height < father.father.y + father.father.height) 255f
        else {
            (max(0f, 255f - (((y + height) - (father.father.y + father.father.height)) / height).coerceIn(0f, 1f) * 255f)).coerceIn(0f, 255f)
        }
        if (alpha.absoluteValue <= 5f) return
        RenderSystem.matrixLayer.scope {
            translatef(x, y, 0f)
            drawValue(mouseX, mouseY, alpha.toInt())
            val titleY = (BASE_HEIGHT - UnicodeFontManager.CURRENT_FONT.height) / 2
            UnicodeFontManager.CURRENT_FONT.drawText((setting as LabelSetting).label(), MINOR_X_PADDING,
                titleY, ColorRGBA.WHITE.alpha(alpha.toInt()))
        }
    }

    override fun drawValue(mouseX: Int, mouseY: Int, fatherAlpha: Int) {}

    context(MatrixLayerStack.MatrixScope)
    override fun drawInfo() {
        if (mc.screen != this.father.father.father) return

        val mouseX = mc.mouseHandler.xpos() * RenderSystem.scaledWidth / RenderSystem.widthD
        val mouseY = mc.mouseHandler.ypos() * RenderSystem.scaledHeight / RenderSystem.heightD

        val info = TextComponent()
        info.addLine("Name: ${setting.localizedName}")
        info.addLine((setting as LabelSetting).label())
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
}