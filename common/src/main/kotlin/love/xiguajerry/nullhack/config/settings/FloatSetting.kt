package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate
import love.xiguajerry.nullhack.i18n.I18N

class FloatSetting(
    translateKey: String, i18N: I18N,
    defaultValue: Float, range: ClosedRange<Float>, step: Float = 0.1f,
    description: String = "",
    visibility: Predicate<Float>,
    onModified: MutableList<BiPredicate<Float, Float>> = mutableListOf(),
    transformer: Combiner<Float>,
    defaultName: String = translateKey
) : AbstractSteppingRangedSetting<Float, FloatSetting>(
    translateKey, i18N,
    defaultValue, range, step,
    description, visibility,
    onModified, transformer,
    defaultName
) {
    override fun readJson(json: JsonElement) {
        value = json.asFloat
    }

    override fun writeJson(json: JsonObject) {
        json.addProperty(nameAsString, value)
    }
}