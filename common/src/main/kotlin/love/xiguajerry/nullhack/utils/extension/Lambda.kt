package love.xiguajerry.nullhack.utils.extension

interface BoolComputing<T> {
    fun bAnd(t1: T, t2: T): T

    fun bOr(t1: T, t2: T): T
}

object BooleanBoolComputing : BoolComputing<Boolean> {
    override fun bAnd(t1: Boolean, t2: Boolean): Boolean {
        return t1 && t2
    }

    override fun bOr(t1: Boolean, t2: Boolean): Boolean {
        return t1 or t2
    }
}

context (BoolComputing<R>)
fun <P0, R> ((P0) -> R).and(other: (P0) -> R): (P0) -> R = { bAnd(this(it), other(it)) }

context (BoolComputing<R>)
fun <P0, R> ((P0) -> R).or(other: (P0) -> R): (P0) -> R = { bOr(this(it), other(it)) }