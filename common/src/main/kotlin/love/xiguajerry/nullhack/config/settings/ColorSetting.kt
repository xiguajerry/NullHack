package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate
import love.xiguajerry.nullhack.i18n.I18N
import love.xiguajerry.nullhack.graphics.color.ColorRGBA

class ColorSetting(
    translateKey: String, i18N: I18N,
    defaultValue: ColorRGBA, description: String,
    visibility: Predicate<ColorRGBA>,
    onModified: MutableList<BiPredicate<ColorRGBA, ColorRGBA>>,
    transformer: Combiner<ColorRGBA>,
    defaultName: String = translateKey
) : AbstractSetting<ColorRGBA, ColorSetting>(
    translateKey, i18N,
    defaultValue, description,
    visibility, onModified, transformer,
    defaultName
) {
    override fun writeJson(json: JsonObject) {
        json.addProperty(nameAsString, value.toString())
    }

    override fun readJson(json: JsonElement) {
        value = ColorRGBA.fromString(json.asString)
    }
}