package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.i18n.I18N
import love.xiguajerry.nullhack.utils.Predicate

class LabelSetting(
    translateKey: String, i18N: I18N,
    description: String = "",
    visibility: Predicate<Unit>,
    defaultName: String = translateKey,
    val label: () -> String
) : AbstractSetting<Unit, LabelSetting>(
    translateKey, i18N,
    Unit, description,
    visibility, mutableListOf(), { _, _ -> },
    defaultName
) {
    override fun chooseJsonElement(json: JsonObject): JsonElement? = null
    override fun readJson(json: JsonElement) {}
    override fun writeJson(json: JsonObject) {}
}