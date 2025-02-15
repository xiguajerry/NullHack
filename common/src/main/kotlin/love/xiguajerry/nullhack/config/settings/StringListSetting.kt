package love.xiguajerry.nullhack.config.settings

import love.xiguajerry.nullhack.i18n.I18N
import love.xiguajerry.nullhack.utils.BiPredicate
import love.xiguajerry.nullhack.utils.Combiner
import love.xiguajerry.nullhack.utils.Predicate

class StringListSetting(
    translateKey: String, i18N: I18N,
    defaultValue: List<String>, description: String,
    visibility: Predicate<List<String>>,
    onModified: MutableList<BiPredicate<List<String>, List<String>>>,
    transformer: Combiner<List<String>>,
    defaultName: String = translateKey
) : AbstractListSetting<String>(
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