package love.xiguajerry.nullhack.i18n

interface ILocalized {
    val i18N: I18N
    val translateKey: String

    fun resolve(subKey: String): String
}