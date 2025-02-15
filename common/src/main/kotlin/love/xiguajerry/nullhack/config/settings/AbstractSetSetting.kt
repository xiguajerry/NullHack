package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.i18n.I18N
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate

abstract class AbstractSetSetting<E>(
    translateKey: String, i18N: I18N,
    defaultValue: Set<E>, description: String,
    visibility: Predicate<Set<E>>,
    onModified: MutableList<BiPredicate<Set<E>, Set<E>>>,
    transformer: Combiner<Set<E>>,
    defaultName: String = translateKey
) : AbstractSetting<Set<E>, AbstractSetSetting<E>>(
    translateKey, i18N,
    defaultValue, description,
    visibility, onModified, transformer,
    defaultName
) {
    fun editValue(transformer: (MutableSet<E>) -> Unit) {
        val newValue = value.toMutableSet()
        transformer(newValue)
        this.value = newValue
    }

    override fun readJson(json: JsonElement) {
        value = buildSet {
            json.asJsonArray.forEach {
                add(string2Element(it.asString))
            }
        }
    }

    override fun writeJson(json: JsonObject) {
        val array = JsonArray().apply {
            value.forEach {
                add(element2String(it))
            }
        }
        json.add(nameAsString, array)
    }

    abstract fun element2String(element: E): String
    abstract fun string2Element(string: String): E
}