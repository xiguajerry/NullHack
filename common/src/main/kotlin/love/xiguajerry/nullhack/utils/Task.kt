package love.xiguajerry.nullhack.utils

import love.xiguajerry.nullhack.NullHackMod

fun interface Task<R> {
    fun run(): R

    fun toNamedTask(name: CharSequence = "${NullHackMod.NAME}-DefaultTaskName"): NamedTask<R> {
        return NamedTask { this@Task.run() }
    }
}

fun interface NamedTask<R> : Task<R>, Nameable {
    override val name: CharSequence
        get() = "${NullHackMod.NAME}-DefaultTaskName"
}