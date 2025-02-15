package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate
import love.xiguajerry.nullhack.i18n.I18N

class BooleanSetting(
    translateKey: String, i18N: I18N,
    defaultValue: Boolean, description: String = "",
    visibility: Predicate<Boolean>,
    onModified: MutableList<BiPredicate<Boolean, Boolean>>,
    transformer: Combiner<Boolean>,
    defaultName: String = translateKey
) : AbstractSetting<Boolean, BooleanSetting>(
    translateKey, i18N,
    defaultValue, description,
    visibility, onModified, transformer,
    defaultName
) {
    override fun readJson(json: JsonElement) {
        value = json.asBoolean
    }

    override fun writeJson(json: JsonObject) {
        json.addProperty(nameAsString, value)
    }
}