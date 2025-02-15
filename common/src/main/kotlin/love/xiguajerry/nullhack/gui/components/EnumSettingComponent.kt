package love.xiguajerry.nullhack.gui.components

import love.xiguajerry.nullhack.gui.NullHackGui
import love.xiguajerry.nullhack.config.settings.AbstractSetting
import love.xiguajerry.nullhack.config.settings.EnumSetting

@Suppress("UNCHECKED_CAST")
class EnumSettingComponent<G : NullHackGui<G>>(
    x: Float, y: Float, width0: Float,
    setting: AbstractSetting<Enum<*>, *>, father: ModuleComponent<G>
) : CommonSettingComponent<Enum<*>, G>(x, y, width0, setting, father) {
    private val enumSetting = setting as EnumSetting<*>
    private val values = (enumSetting.value::class.java.enumConstants
        ?: enumSetting.value::class.java.superclass.getDeclaredMethod("\$values")
            .also { it.isAccessible = true }(null) as Array<out Enum<*>>).toList()

    private fun getNext(backwards: Boolean = false): Enum<*> {
        val dest = values.indexOf(enumSetting.value) + if (backwards) -1 else 1
        return values[if (dest < 0) values.size + dest else dest % values.size]
    }

    override fun clicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (isHovered(mouseX, mouseY)) {
            val next = when (mouseButton) {
                0 -> getNext(false)
                1 -> getNext(true)
                else -> null
            }
            setting.value = (next ?: return false)
            return true
        }
        return super.clicked(mouseX, mouseY, mouseButton)
    }
}