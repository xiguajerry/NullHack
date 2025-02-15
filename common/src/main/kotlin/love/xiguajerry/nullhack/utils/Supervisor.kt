package love.xiguajerry.nullhack.utils

class Supervisor {
    var errorOccurred = false

    operator fun <R> invoke(op: Supervisor.() -> R): R = this.op()
}