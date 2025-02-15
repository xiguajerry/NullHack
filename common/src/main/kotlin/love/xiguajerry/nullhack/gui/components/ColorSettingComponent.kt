package love.xiguajerry.nullhack.gui.components

import love.xiguajerry.nullhack.config.settings.ColorSetting
import love.xiguajerry.nullhack.gui.ColorPicker
import love.xiguajerry.nullhack.gui.NullHackGui
import love.xiguajerry.nullhack.utils.MinecraftWrapper
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.matrix.scope
import love.xiguajerry.nullhack.graphics.matrix.translatef
import love.xiguajerry.nullhack.utils.input.CursorStyle
import org.lwjgl.glfw.GLFW
import kotlin.math.min
import love.xiguajerry.nullhack.RenderSystem as RS

class ColorSettingComponent<G : NullHackGui<G>>(
    x: Float, y: Float, width0: Float,
    setting: ColorSetting, father: ModuleComponent<G>
) : CommonSettingComponent<ColorRGBA, G>(x, y, width0, setting, father) {
    override val height: Float
        get() = animationHeight.getAndUpdate(if (extended) height0 else BASE_HEIGHT)
    private val colorPicker = ColorPicker(
        setting,
        width0 - 3 - ColorPicker.GAP - ColorPicker.RGB_WIDTH - ColorPicker.GAP - ColorPicker.ALPHA_WIDTH,
        father.father.father
    )
    override var height0: Float = BASE_HEIGHT + colorPicker.actualHeight + 1.0f
    private var firstOpen = true
    override val cursorStyle: CursorStyle
        get() = if (colorPicker.isSliding) CursorStyle.CROSSHAIR else super.cursorStyle

    override fun init() {
        if (firstOpen) {
            firstOpen = false
            colorPicker.update()
        }
    }

    override fun render(mouseX: Int, mouseY: Int) {
        super.render(mouseX, mouseY)
        applyScissor {
            drawColorPlate(mouseX, mouseY, MinecraftWrapper.mc.deltaTracker.getGameTimeDeltaPartialTick(true))
        }
    }

    override fun handleMouseInput(mouseX: Int, mouseY: Int): Boolean {
        return if (colorPicker.isSliding) {
            GLFW.glfwSetCursor(MinecraftWrapper.mc.window.window, cursorStyle.cursor)
            true
        } else false
    }

    override fun drawValue(mouseX: Int, mouseY: Int, fatherAlpha: Int) {
        drawColorPreview(fatherAlpha)
    }

    private fun drawColorPlate(mouseX: Int, mouseY: Int, delta: Float) {
        colorPicker.x = x + 1f
        colorPicker.y = y + BASE_HEIGHT + 1
        colorPicker.headless = true
        colorPicker.render(null, mouseX, mouseY, delta)
    }

    private fun drawColorPreview(fatherAlpha: Int) {
        RS.matrixLayer.scope {
            val rectW = 12f
            val rectH = 7f
            val x = (width - rectW) - MINOR_X_PADDING
            val y = (BASE_HEIGHT - rectH) / 2
            translatef(x, y, 0f)
            Render2DUtils.drawRect(rectW, rectH, setting.value.alpha(min(fatherAlpha, setting.value.a)))
            Render2DUtils.drawRectOutline(rectW, rectH, OUTLINE_WIDTH / 2, ColorRGBA.WHITE.alpha(fatherAlpha))
        }
    }

    override fun clicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (isHovered(mouseX, mouseY)) {
            if (mouseButton == 1) {
                extended = !extended
            }
            return true
        } else {
            if (extended && colorPicker.mouseClicked(mouseX.toDouble(), mouseY.toDouble(), mouseButton)) return true
        }
        return super.clicked(mouseX, mouseY, mouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        colorPicker.mouseReleased(mouseX.toDouble(), mouseY.toDouble(), button)
    }
}