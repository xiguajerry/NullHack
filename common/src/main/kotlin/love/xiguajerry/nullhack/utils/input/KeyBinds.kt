package love.xiguajerry.nullhack.utils.input

import com.mojang.blaze3d.platform.InputConstants
import love.xiguajerry.nullhack.NullHackMod
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW


object KeyBinds {
    private const val CATEGORY = NullHackMod.NAME
    var OPEN_GUI: KeyMapping =
        KeyMapping("Click Gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY)

    fun apply(binds: Array<KeyMapping>): Array<KeyMapping> {
        // Add category
        val categories: MutableMap<String, Int> = KeyMapping.CATEGORY_SORT_ORDER
        var highest = 0
        for (i in categories.values) {
            if (i > highest) highest = i
        }
        categories[CATEGORY] = highest + 1

        // Add key binding
        val newBinds = arrayOfNulls<KeyMapping>(binds.size + 1)
        System.arraycopy(binds, 0, newBinds, 0, binds.size)
        newBinds[binds.size] = OPEN_GUI
        return newBinds.requireNoNulls()
    }

    fun getKey(bind: KeyMapping): Int {
        return bind.defaultKey.value
    }
}