package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.i18n.I18N
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.NonNullContext
import love.xiguajerry.nullhack.utils.Predicate
import love.xiguajerry.nullhack.utils.input.KeyBind

class BindSetting(
    translateKey: String, i18N: I18N,
    defaultValue: KeyBind, description: String = "",
    visibility: Predicate<KeyBind>,
    onModified: MutableList<BiPredicate<KeyBind, KeyBind>> = mutableListOf(),
    transformer: Combiner<KeyBind>,
    defaultName: String = translateKey,
    val alwaysActive: Boolean = false
) : AbstractSetting<KeyBind, BindSetting>(
    translateKey, i18N,
    defaultValue, description,
    visibility, onModified, transformer,
    defaultName
) {
    val keyName get() = covertCodeToString(value)
    var isPressed = false
    val onPressConsumers: MutableList<NonNullContext.() -> Unit> = ArrayList()

    fun onPress(consumer: NonNullContext.() -> Unit) {
        this.onPressConsumers.add(consumer)
    }

    companion object {
        fun covertCodeToString(key: KeyBind): String = key.keyName.uppercase()
    }

    override fun readJson(json: JsonElement) {
        value = KeyBind(keyCode = json.asInt)
    }

    override fun writeJson(json: JsonObject) {
        json.addProperty(nameAsString, value.keyCode)
    }
}