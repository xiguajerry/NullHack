package love.xiguajerry.nullhack.command.args

import love.xiguajerry.nullhack.utils.Nameable

/**
 * The ID for an argument
 */
@Suppress("UNUSED")
data class ArgIdentifier<T : Any>(override val name: CharSequence) : Nameable
