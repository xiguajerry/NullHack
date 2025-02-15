package love.xiguajerry.nullhack.gui.components

import love.xiguajerry.nullhack.config.settings.AbstractSetting
import love.xiguajerry.nullhack.gui.NullHackGui
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.graphics.animations.AnimationFlag
import love.xiguajerry.nullhack.graphics.animations.Easing
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils

class BooleanSettingComponent<G : NullHackGui<G>>(
    x: Float, y: Float, width0: Float,
    setting: AbstractSetting<Boolean, *>, father: ModuleComponent<G>
) : CommonSettingComponent<Boolean, G>(x, y, width0, setting, father) {
    private val animationX = AnimationFlag(Easing.OUT_CUBIC, DEFAULT_WIDTH_ANIM)

    override fun render(mouseX: Int, mouseY: Int) {
        if (!setting.isVisible || !father.extended) animationX.getAndUpdate(0f)
        super.render(mouseX, mouseY)
    }

    override fun drawValue(mouseX: Int, mouseY: Int, fatherAlpha: Int) {
        val rectW = animationX.getAndUpdate(if (setting.value) width else 0f)
        if (rectW < 0.1f) return
        Render2DUtils.drawRect(
            HORIZONTAL_MARGIN, VERTICAL_MARGIN, rectW - HORIZONTAL_MARGIN,
            BASE_HEIGHT - VERTICAL_MARGIN, GuiManager.getColor(father.index)
        )
    }

    override fun clicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
            setting.value = !setting.value
            return true
        }
        return super.clicked(mouseX, mouseY, mouseButton)
    }
}