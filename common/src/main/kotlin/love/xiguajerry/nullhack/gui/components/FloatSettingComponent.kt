package love.xiguajerry.nullhack.gui.components

import love.xiguajerry.nullhack.config.settings.FloatSetting
import love.xiguajerry.nullhack.gui.NullHackGui
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.utils.MinecraftWrapper
import love.xiguajerry.nullhack.graphics.animations.AnimationFlag
import love.xiguajerry.nullhack.graphics.animations.Easing
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils
import love.xiguajerry.nullhack.utils.input.CursorStyle
import org.lwjgl.glfw.GLFW
import kotlin.math.absoluteValue

class FloatSettingComponent<G : NullHackGui<G>>(
    x: Float, y: Float, width0: Float,
    private val _setting: FloatSetting, father: ModuleComponent<G>
) : CommonSettingComponent<Float, G>(x, y, width0, _setting, father) {
    private var dragging = false
    private val animationX = AnimationFlag(Easing.OUT_CUBIC, DEFAULT_WIDTH_ANIM)
    override val cursorStyle: CursorStyle
        get() = CursorStyle.DRAG_X

    private fun getWidthMultiplier() = (_setting.value - _setting.range.start) / (_setting.range.endInclusive - _setting.range.start)

    override fun render(mouseX: Int, mouseY: Int) {
        if (!setting.isVisible || !father.extended) animationX.getAndUpdate(0f)
        if (GLFW.glfwGetMouseButton(MinecraftWrapper.mc.window.window, GLFW.GLFW_MOUSE_BUTTON_1)
            != GLFW.GLFW_PRESS) dragging = false
        if (dragging) {
            val segWidth = _setting.step / (_setting.range.endInclusive - _setting.range.start) * width0
            val mouseHoldingOffset = (mouseX - x).coerceIn(0F, width0)
            var nearestStop = 0f
            var current = 0
            while (true) {
                val next = nearestStop + segWidth
                if (next < mouseHoldingOffset) {
                    nearestStop = next
                    current++
                    continue
                }
                if (mouseHoldingOffset in nearestStop..next) {
                    val leftAbs = mouseHoldingOffset - nearestStop
                    val rightAbs = next - mouseHoldingOffset
                    if (leftAbs.absoluteValue > rightAbs.absoluteValue) {
                        current++
                    }
                    break
                }
            }
            _setting.value = (_setting.range.start + current * _setting.step)
                .coerceIn(_setting.range.start, _setting.range.endInclusive)
        }
        super.render(mouseX, mouseY)
    }

    override fun drawValue(mouseX: Int, mouseY: Int, fatherAlpha: Int) {
        val width = animationX.getAndUpdate(
            if (!setting.isVisible || !father.extended) 0f
            else width0 * getWidthMultiplier()
        ) * (width / width0)
        if (width.absoluteValue > 0.1f)
            Render2DUtils.drawRect(
                HORIZONTAL_MARGIN, VERTICAL_MARGIN, width - HORIZONTAL_MARGIN,
                BASE_HEIGHT - VERTICAL_MARGIN, GuiManager.getColor(father.index).alpha(fatherAlpha))
        super.drawValue(mouseX, mouseY, fatherAlpha)
    }

    override fun clicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (isHovered(mouseX, mouseY) && mouseButton == 0) {
            dragging = true
            return true
        }
        return super.clicked(mouseX, mouseY, mouseButton)
    }
}