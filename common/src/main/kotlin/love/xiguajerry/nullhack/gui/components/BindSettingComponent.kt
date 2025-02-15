package love.xiguajerry.nullhack.gui.components

import love.xiguajerry.nullhack.config.settings.BindSetting
import love.xiguajerry.nullhack.gui.NullHackGui
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.utils.input.KeyBind
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN
import kotlin.math.max

class BindSettingComponent<G : NullHackGui<G>>(
    x: Float, y: Float, width0: Float,
    setting: BindSetting, father: ModuleComponent<G>
) : CommonSettingComponent<KeyBind, G>(x, y, width0, setting, father) {
    private var listening = false

    override fun onKeyTyped(typedChar: Char, keyCode: Int) {
        if (listening && typedChar == '\u0000') {
            setting.value = when (keyCode) {
                GLFW.GLFW_KEY_ESCAPE -> KeyBind.NONE
                GLFW.GLFW_KEY_DELETE -> KeyBind.NONE
                GLFW.GLFW_KEY_BACKSPACE -> KeyBind.NONE
                else -> KeyBind(keyCode = keyCode)
            }
            listening = false
        }
    }

    override fun drawValue(mouseX: Int, mouseY: Int, fatherAlpha: Int) {
        val str = when {
            listening -> "..."
            setting.value.keyCode == GLFW_KEY_UNKNOWN -> "NONE"
            else -> (setting as BindSetting).keyName
        }
        val x = max(width - UnicodeFontManager.CURRENT_FONT.getWidth(str) - MINOR_X_PADDING, 0f)
        val y = (BASE_HEIGHT - UnicodeFontManager.CURRENT_FONT.height) / 2
        UnicodeFontManager.CURRENT_FONT.drawText(str, x, y,
            (if (listening) GuiManager.getColor(father.index) else ColorRGBA.WHITE).alpha(fatherAlpha))
    }

    override fun clicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (isHovered(mouseX, mouseY) && mouseButton == 0) {
            listening = !listening
            return true
        }
        return super.clicked(mouseX, mouseY, mouseButton)
    }
}