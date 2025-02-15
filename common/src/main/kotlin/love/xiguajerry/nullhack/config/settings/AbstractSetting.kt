package love.xiguajerry.nullhack.config.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import love.xiguajerry.nullhack.i18n.I18N
import love.xiguajerry.nullhack.utils.*
import love.xiguajerry.nullhack.i18n.LocalizedNameable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class AbstractSetting<V, S : AbstractSetting<V, S>>(
    translateKey: String, i18N: I18N,
    val defaultValue: V,
    override val description: String,
    val visibility: Predicate<V>,
    protected val onModified: MutableList<BiPredicate<V, V>>,
    protected val transformer: Combiner<V>,
    defaultName: String = translateKey
) : LocalizedNameable(translateKey, i18N, defaultName), Describable, ReadWriteProperty<Any?, V> {
    open var value: V = defaultValue
        set(value) {
            val prev = field
            if (onModified.all {
                it(prev, value)
            }) field = transformer(prev, value)
        }

    val isVisible get() = visibility(value)

    fun clearConsumers() = onModified.clear()

    fun register(consumer: BiPredicate<V, V>): S {
        onModified.add(consumer)
        return this as S
    }

    fun setToDefault() {
        this.value = defaultValue
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V = value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        this.value = value
    }

    open fun chooseJsonElement(json: JsonObject): JsonElement? = json[nameAsString] ?: json[translateKey]

    abstract fun writeJson(json: JsonObject)
    abstract fun readJson(json: JsonElement)
}