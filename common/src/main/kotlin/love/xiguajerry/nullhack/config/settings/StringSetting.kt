package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate
import love.xiguajerry.nullhack.i18n.I18N

class StringSetting(
    translateKey: String, i18N: I18N,
    defaultValue: String, description: String = "",
    visibility: Predicate<String>,
    onModified: MutableList<BiPredicate<String, String>> = mutableListOf(),
    transformer: Combiner<String>,
    defaultName: String = translateKey
) : AbstractSetting<String, StringSetting>(
    translateKey, i18N,
    defaultValue, description,
    visibility, onModified, transformer,
    defaultName
) {
    override fun readJson(json: JsonElement) {
        value = json.asString
    }

    override fun writeJson(json: JsonObject) {
        json.addProperty(nameAsString, value)
    }
}