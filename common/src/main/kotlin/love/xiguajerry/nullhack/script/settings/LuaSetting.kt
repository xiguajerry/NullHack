package love.xiguajerry.nullhack.script.settings

import love.xiguajerry.nullhack.config.settings.*
import love.xiguajerry.nullhack.script.toLua
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.utils.input.KeyBind
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.ZeroArgFunction

class LuaSetting<T : Any>(val setting: AbstractSetting<T, *>) : LuaTable() {
    init {
        this["getValue"] = object : ZeroArgFunction() {
            override fun call(): LuaValue {
                return setting.value.toLua()
            }
        }
        this["setValue"] = object : OneArgFunction() {
            override fun call(value: LuaValue): LuaValue {
                when (setting) {
                    is BindSetting -> {
                        try {
                            val keycode = value.checkint()
                            setting.value = KeyBind(keyCode = keycode)
                        } catch (e: Exception) {
                            val keyName = value.checkjstring()
                            setting.value = KeyBind.fromRaw(keyName)
                        }
                    }
                    is BooleanSetting -> setting.value = value.checkboolean()
                    is ColorSetting -> setting.value = ColorRGBA.fromString(value.checkjstring())
                    is DoubleSetting -> setting.value = value.checkdouble()
                    is FloatSetting -> setting.value = value.checkdouble().toFloat()
                    is EnumSetting -> setting.setWithName(value.checkjstring())
                    is IntSetting -> setting.value = value.checkint()
                    is StringSetting -> setting.value = value.checkjstring()
                    else -> throw LuaError("Unsupported operation")
                }
                return LuaValue.NIL
            }
        }
    }
}