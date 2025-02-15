package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate
import love.xiguajerry.nullhack.i18n.I18N

class IntSetting(
    translateKey: String, i18N: I18N,
    defaultValue: Int, range: ClosedRange<Int>, step: Int = 1,
    description: String = "",
    visibility: Predicate<Int>,
    onModified: MutableList<BiPredicate<Int, Int>> = mutableListOf(),
    transformer: Combiner<Int>,
    defaultName: String = translateKey
) : AbstractSteppingRangedSetting<Int, IntSetting>(
    translateKey, i18N,
    defaultValue, range, step,
    description, visibility,
    onModified, transformer,
    defaultName
) {
    override fun writeJson(json: JsonObject) {
        json.addProperty(nameAsString, value)
    }

    override fun readJson(json: JsonElement) {
        value = json.asInt
    }
}