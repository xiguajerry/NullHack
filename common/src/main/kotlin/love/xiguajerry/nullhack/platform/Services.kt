package love.xiguajerry.nullhack.platform

import love.xiguajerry.nullhack.NullHackMod
import java.util.*
import java.util.function.Supplier

object Services {
    val PLATFORM = load(IModLoaderPlatform::class.java)

    private fun <T> load(clazz: Class<out T>): T {
        val loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow { NullPointerException("Failed to load service for " + clazz.getName()) }
        NullHackMod.LOGGER.debug("Loaded {} for service {}", loadedService, clazz)
        return loadedService
    }
}