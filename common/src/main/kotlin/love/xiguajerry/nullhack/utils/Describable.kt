package love.xiguajerry.nullhack.utils

interface Describable {
    val description: CharSequence

    val descString
        get() = description.toString()
}