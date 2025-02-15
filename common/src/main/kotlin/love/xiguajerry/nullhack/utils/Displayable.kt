package love.xiguajerry.nullhack.utils

interface Displayable {
    val displayName: CharSequence
        get() = this.toString()

    val displayString: String
        get() = displayName.toString()
}