package love.xiguajerry.nullhack.i18n

import love.xiguajerry.nullhack.utils.Nameable

interface ILocalizedNameable : ILocalized, Nameable {
    /**
     * Localized name, should only be used for rendering purposes
     */
    val localizedName: String
}