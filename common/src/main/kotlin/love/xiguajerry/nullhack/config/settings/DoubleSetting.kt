package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate
import love.xiguajerry.nullhack.i18n.I18N

class DoubleSetting(
    translateKey: String, i18N: I18N,
    defaultValue: Double, range: ClosedRange<Double>, step: Double = 0.1,
    description: String = "",
    visibility: Predicate<Double>,
    onModified: MutableList<BiPredicate<Double, Double>> = mutableListOf(),
    transformer: Combiner<Double>,
    defaultName: String = translateKey
) : AbstractSteppingRangedSetting<Double, DoubleSetting>(
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
        value = json.asDouble
    }
}