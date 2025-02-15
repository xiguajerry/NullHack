package love.xiguajerry.nullhack.event

import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.utils.Reflections
import love.xiguajerry.nullhack.utils.shortName

object EventClasses {
    val classes: Set<Class<out IEvent>> = Reflections.getSubTypesOf(IEvent::class.java)

    init {
        NullHackMod.LOGGER.info("Available events:")
        for (clz in classes) {
            NullHackMod.LOGGER.info("\t ${clz.shortName}")
        }
    }
}