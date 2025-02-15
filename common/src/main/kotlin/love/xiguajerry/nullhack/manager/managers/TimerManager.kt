package love.xiguajerry.nullhack.manager.managers

import love.xiguajerry.nullhack.event.api.AlwaysListening
import love.xiguajerry.nullhack.event.api.handler
import love.xiguajerry.nullhack.event.impl.LoopEvent
import love.xiguajerry.nullhack.event.impl.TickEvent
import love.xiguajerry.nullhack.manager.AbstractManager
import love.xiguajerry.nullhack.modules.AbstractModule
import love.xiguajerry.nullhack.utils.MinecraftWrapper.mc
import love.xiguajerry.nullhack.utils.extension.lastValueOrNull
import love.xiguajerry.nullhack.utils.extension.synchronized
import love.xiguajerry.nullhack.utils.runSafe
import java.util.TreeMap
import kotlin.math.roundToInt

object TimerManager : AbstractManager(), AlwaysListening {
    private val modifiers = TreeMap<AbstractModule, Modifier>().synchronized()
    private var modified = false

    var globalTicks = Int.MIN_VALUE; private set
    var tickLength = 50.0f; private set

    init {
        handler<LoopEvent.Start>(Int.MAX_VALUE, true) {
            runSafe {
                synchronized(modifiers) {
                    modifiers.values.removeIf { it.endTick < globalTicks }
                    modifiers.lastValueOrNull()?.let {
                        world.tickRateManager().setTickRate(1000 / it.tickLength)
                    } ?: return@runSafe null
                }

                modified = true
            } ?: run {
                modifiers.clear()
            }

            tickLength = mc.level?.tickRateManager()?.tickrate()?.let { 1000 / it } ?: 50f
        }

        handler<TickEvent.Pre>(Int.MAX_VALUE, true) {
            globalTicks++
        }
    }

    fun AbstractModule.resetTimer() {
        modifiers.remove(this)
    }

    fun AbstractModule.modifyTimer(tickLength: Float, timeoutTicks: Int = 1) {
        runSafe {
            modifiers[this@modifyTimer] =
                Modifier(tickLength, globalTicks + mc.deltaTracker.getGameTimeDeltaPartialTick(true).roundToInt() + timeoutTicks)
        }
    }

    private class Modifier(
        val tickLength: Float,
        val endTick: Int
    )
}