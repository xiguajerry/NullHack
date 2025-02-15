package love.xiguajerry.nullhack.config.settings

import love.xiguajerry.nullhack.i18n.I18N
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate

class StringSetSetting(
    translateKey: String, i18N: I18N,
    defaultValue: Set<String>, description: String,
    visibility: Predicate<Set<String>>,
    onModified: MutableList<BiPredicate<Set<String>, Set<String>>>,
    transformer: Combiner<Set<String>>,
    defaultName: String = translateKey
) : AbstractSetSetting<String>(
    translateKey, i18N,
    defaultValue, description,
    visibility, onModified, transformer,
    defaultName
) {
    override fun element2String(element: String): String {
        return element
    }

    override fun string2Element(string: String): String {
        return string
    }
}