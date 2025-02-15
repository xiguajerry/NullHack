package love.xiguajerry.nullhack.utils

interface Alias : Nameable {
    override val alias: Set<CharSequence>

    override val allNames: Set<CharSequence>
        get() = mutableSetOf(name).apply {
            addAll(alias)
        }
}