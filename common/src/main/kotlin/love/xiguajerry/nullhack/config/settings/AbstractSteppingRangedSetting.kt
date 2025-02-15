package love.xiguajerry.nullhack.config.settings

import love.xiguajerry.nullhack.i18n.I18N
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate

abstract class AbstractSteppingRangedSetting<V : Comparable<V>, S : AbstractSteppingRangedSetting<V, S>>(
    translateKey: String, i18N: I18N,
    defaultValue: V, range: ClosedRange<V>, val step: V,
    description: String, visibility: Predicate<V>,
    onModified: MutableList<BiPredicate<V, V>>, transformer: Combiner<V>,
    defaultName: String = translateKey
) : AbstractRangedSetting<V, S>(
    translateKey, i18N,
    defaultValue, range, description,
    visibility, onModified, transformer,
    defaultName
)