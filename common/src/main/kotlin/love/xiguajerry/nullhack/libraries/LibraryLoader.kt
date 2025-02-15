package love.xiguajerry.nullhack.libraries

import kotlinx.coroutines.launch
import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.utils.threads.Coroutine

object LibraryLoader {
    private val libraries = ArrayList<AbstractLibrary>()
    val loadFlags: BooleanArray

    init {
        libraries.run {
//            add(ImGui)
        }
        loadFlags = BooleanArray(libraries.size)
    }

    fun load() {
        val arch = Architecture.detectArchitecture()
        val os = OS.detectOs()

        libraries.forEachIndexed { index, lib ->
            Coroutine.launch {
                try {
                    if (!lib.load(arch, os)) {
                        throw UnsatisfiedLinkError()
                    } else {
                        NullHackMod.LOGGER.info("Library ${lib.namespace} was loaded successfully")
                        loadFlags[index] = true
                    }
                } catch (e: Throwable) {
                    NullHackMod.LOGGER.error("Failed to load library ${lib.namespace}.", e)
                }
            }
        }
    }
}

