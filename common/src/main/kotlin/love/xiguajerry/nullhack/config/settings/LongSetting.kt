package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate
import love.xiguajerry.nullhack.i18n.I18N

class LongSetting(
    translateKey: String, i18N: I18N,
    defaultValue: Long, range: ClosedRange<Long>, step: Long = 1,
    description: String = "",
    visibility: Predicate<Long>,
    onModified: MutableList<BiPredicate<Long, Long>> = mutableListOf(),
    transformer: Combiner<Long>,
    defaultName: String = translateKey
) : AbstractSteppingRangedSetting<Long, LongSetting>(
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
        value = json.asLong
    }
}