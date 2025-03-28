package love.xiguajerry.nullhack.command

import love.xiguajerry.nullhack.command.args.FinalArg
import love.xiguajerry.nullhack.command.execute.IExecuteEvent
import love.xiguajerry.nullhack.utils.Alias
import love.xiguajerry.nullhack.utils.Nameable

/**
 * Command built from [CommandBuilder], this shouldn't be used
 * directly for instance creation in implementation.
 *
 * @param E Type of [IExecuteEvent], can be itself or its subtype
 * @param name Name of this [Command], used to call the [Command] or identifying
 * @param alias Alias of [Command], functions the same as [name]
 * @param description Description of this [Command]
 * @param finalArgs Possible argument combinations of this [Command]
 */
class Command<E : IExecuteEvent> internal constructor(
    override val name: CharSequence,
    override val alias: Set<CharSequence>,
    val description: String,
    val finalArgs: Array<FinalArg<E>>,
    val builder: CommandBuilder<E>
) : Nameable, Alias, Invokable<E> {
    /**
     * Invoke this [Command] with [event].
     *
     * @param event Event being used for invoking, must match the type [E]
     *
     * @throws SubCommandNotFoundException if no sub command is found
     */
    override suspend fun invoke(event: E) {
        finalArgs.firstOrNull { it.checkArgs(event.args) }?.invoke(event)
            ?: throw SubCommandNotFoundException(event.args, this)
    }

    /**
     * Returns argument help for this [Command].
     */
    fun printArgHelp(): String {
        return finalArgs.joinToString("\n\n") {
            var argHelp = it.printArgHelp()
            val description = it.toString()

            if (argHelp.isBlank()) argHelp = "<No Argument>"
            if (description.isNotBlank()) argHelp += "\n$it"
            argHelp
        }
    }

    override fun equals(other: Any?) = this === other
        || (other is Command<*>
        && name == other.name
        && alias.toTypedArray().contentEquals(other.alias.toTypedArray())
        && description == other.description
        && finalArgs.contentEquals(other.finalArgs)
        && builder == other.builder)

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + alias.toTypedArray().contentHashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + finalArgs.contentHashCode()
        return 31 * result + builder.hashCode()
    }

}
