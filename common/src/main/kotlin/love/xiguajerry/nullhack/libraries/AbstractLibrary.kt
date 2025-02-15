package love.xiguajerry.nullhack.libraries

import love.xiguajerry.nullhack.NullHackMod
import kotlin.io.path.Path

abstract class AbstractLibrary(val namespace: String) {
//    val root = Path("assets", "nullhack", "libraries")
    val root = Path(NullHackMod.ID, "libraries")

    abstract fun load(architecture: Architecture, os: OS): Boolean
}