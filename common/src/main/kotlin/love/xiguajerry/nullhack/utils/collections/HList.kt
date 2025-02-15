package love.xiguajerry.nullhack.utils.collections

import love.xiguajerry.nullhack.utils.collections.HList.Cons
import love.xiguajerry.nullhack.utils.collections.HList.Nil

interface Kind<out F, out A>
typealias Kind2<F, A, B> = Kind<Kind<F, A>, B>
typealias Kind3<F, A, B, C> = Kind<Kind2<F, A, B>, C>
typealias Kind4<F, A, B, C, D> = Kind<Kind3<F, A, B, C>, D>
typealias Kind5<F, A, B, C, D, E> = Kind<Kind4<F, A, B, C, D>, E>
typealias Kind6<F, A, B, C, D, E, G> = Kind<Kind5<F, A, B, C, D, E>, G>
typealias Kind7<F, A, B, C, D, E, G, H> = Kind<Kind6<F, A, B, C, D, E, G>, H>
typealias Kind8<F, A, B, C, D, E, G, H, I> = Kind<Kind7<F, A, B, C, D, E, G, H>, I>
typealias Kind9<F, A, B, C, D, E, G, H, I, J> = Kind<Kind8<F, A, B, C, D, E, G, H, I>, J>
typealias Kind10<F, A, B, C, D, E, G, H, I, J, K> = Kind<Kind9<F, A, B, C, D, E, G, H, I, J>, K>
typealias Kind11<F, A, B, C, D, E, G, H, I, J, K, L> = Kind<Kind10<F, A, B, C, D, E, G, H, I, J, K>, L>
typealias Kind12<F, A, B, C, D, E, G, H, I, J, K, L, M> = Kind<Kind11<F, A, B, C, D, E, G, H, I, J, K, L>, M>
typealias Kind13<F, A, B, C, D, E, G, H, I, J, K, L, M, N> = Kind<Kind12<F, A, B, C, D, E, G, H, I, J, K, L, M>, N>
typealias Kind14<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O> = Kind<Kind13<F, A, B, C, D, E, G, H, I, J, K, L, M, N>, O>
typealias Kind15<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P> = Kind<Kind14<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O>, P>
typealias Kind16<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q> = Kind<Kind15<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P>, Q>
typealias Kind17<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R> = Kind<Kind16<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q>, R>
typealias Kind18<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S> = Kind<Kind17<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R>, S>
typealias Kind19<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = Kind<Kind18<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S>, T>
typealias Kind20<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = Kind<Kind19<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, U>
typealias Kind21<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> = Kind<Kind20<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, V>
typealias Kind22<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W> = Kind<Kind21<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, W>

sealed class HListK<F, A: HListK<F, A>> {
    data object HNilK : HListK<Nothing, HNilK>()

    data class HConsK<F, E, L : HListK<F, L>>(val head: Kind<F, E>, val tail: L) : HListK<F, HConsK<F, E, L>>()
}

sealed interface HList {
    data class Cons<E, H : HList>(val e: E, val next: H) : HList

    data object Nil : HList
}

tailrec fun HList.forEach(op: (Any?) -> Unit) {
    if (this == Nil) return
    this as Cons<*, *>
    op(this.e)
    next.forEach(op)
}

fun <R> HList.flatMap(op: (Any?) -> Iterator<Any?>): List<Any?> = buildList {
    forEach { op(it).forEach { a -> add(a) } }
}

fun HList.toList() = collect(ArrayList())

fun HList.toMutableList() = collect(ArrayList()) as ArrayList<Any?>

fun HList.collect(list: MutableList<Any?>): List<Any?> {
    forEach { list.add(it) }
    return list
}

infix fun <E> E.cons(nil: Nil) = Cons(this, nil)

infix fun <E, H : HList> E.cons(h: H) = Cons(this, h)