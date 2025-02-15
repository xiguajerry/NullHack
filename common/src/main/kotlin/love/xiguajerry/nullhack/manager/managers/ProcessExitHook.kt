package love.xiguajerry.nullhack.manager.managers

import love.xiguajerry.nullhack.utils.NamedTask
import love.xiguajerry.nullhack.utils.Task

object ProcessExitHook {
    private val tasks: MutableList<NamedTask<Unit>> = ArrayList()

    fun register(task: NamedTask<Unit>) {
        tasks.add(task)
    }

    fun register(task: Task<Unit>) {
        tasks.add(task.toNamedTask())
    }

    fun onExit() {
        tasks.forEach(NamedTask<Unit>::run)
        tasks.clear()
    }
}