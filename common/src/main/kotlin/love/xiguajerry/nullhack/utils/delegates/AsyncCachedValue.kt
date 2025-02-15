package love.xiguajerry.nullhack.utils.delegates

import kotlinx.coroutines.launch
import love.xiguajerry.nullhack.utils.threads.Coroutine
import love.xiguajerry.nullhack.utils.timing.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.properties.ReadWriteProperty

class AsyncCachedValue<T>(
    updateTime: Long,
    timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    private val context: CoroutineContext = Coroutine.context,
    block: () -> T
) : CachedValue<T>(updateTime, timeUnit, block), ReadWriteProperty<Any?, T> {

    override fun get(): T {
        val cached = value

        return when {
            cached == null -> {
                block().also { value = it }
            }
            timer.tickAndReset(updateTime) -> {
                Coroutine.launch(context) {
                    value = block()
                }
                cached
            }
            else -> {
                cached
            }
        }
    }

    override fun update() {
        timer.reset()
        Coroutine.launch(context) {
            value = block()
        }
    }
}