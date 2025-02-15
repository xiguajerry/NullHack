package love.xiguajerry.nullhack.graphics.sync

import love.xiguajerry.nullhack.utils.extension.getValue
import love.xiguajerry.nullhack.utils.extension.setValue
import java.util.concurrent.atomic.AtomicBoolean

class Signal {
    private var signaled by AtomicBoolean(false)

    fun trigger() {
        signaled = true
    }

    fun check(): Boolean {
        val state = signaled
        if (state) signaled = false
        return state
    }
}