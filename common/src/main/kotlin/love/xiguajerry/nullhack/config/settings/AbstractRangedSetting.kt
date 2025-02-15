package love.xiguajerry.nullhack.config.settings

import love.xiguajerry.nullhack.i18n.I18N
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate

abstract class AbstractRangedSetting<V : Comparable<V>, S : AbstractRangedSetting<V, S>>(
    translateKey: String, i18N: I18N,
    defaultValue: V, val range: ClosedRange<V>,
    description: String, visibility: Predicate<V>,
    onModified: MutableList<BiPredicate<V, V>>, transformer: Combiner<V>,
    defaultName: String = translateKey
) : AbstractSetting<V, S>(
    translateKey, i18N,
    defaultValue, description,
    visibility, onModified,
    transformer, defaultName
) {
    override var value: V = defaultValue
        set(value) {
            val prev = field
            if (onModified.all {
                    it(prev, value)
                }) field = transformer(prev, value).coerceIn(range)
        }
}