package love.xiguajerry.nullhack.utils

interface Nameable {
    val name: CharSequence
    val alias get() = setOf(name)
    val nameAsString get() = name.toString()

    val internalName: String
        get() = nameAsString.replace(" ", "")

    val allNames: Set<CharSequence>
        get() = setOf(name, internalName)
}