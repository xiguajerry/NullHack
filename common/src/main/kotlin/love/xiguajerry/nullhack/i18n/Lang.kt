package love.xiguajerry.nullhack.i18n

import love.xiguajerry.nullhack.utils.Displayable


enum class Lang(override val displayName: String, val key: String, val minecraft: String) : Displayable {
    ENGLISH("English", "en_us", "en_us"),
    CHINESE_SIMPLIFIED("简体中文", "zh_cn", "zh_cn"),
    CHINESE_TRADITIONAL_TW("繁體中文(台灣)", "zh_tw", "zh_tw"),
    CHINESE_TRADITIONAL_HK("繁體中文(香港)", "zh_hk", "zh_hk"),
    ESPERANTO("Esperanto", "esperanto", "*")
}