package love.xiguajerry.nullhack.libraries

import com.sun.jna.Native
import imgui.ImGui

object ImGui : AbstractLibrary("imgui") {
    override fun load(architecture: Architecture, os: OS): Boolean {
        if (architecture != Architecture.X86_64) throw IllegalStateException("Unsupported arch")
        val tempFile = when (os) {
            OS.WINDOWS -> {
                Native.extractFromResourcePath("/assets/nullhack/libraries/imgui-java64.dll")
            }
            OS.MAC_OS -> {
                Native.extractFromResourcePath("/assets/nullhack/libraries/libimgui-java64.dylib")
            }
            OS.LINUX -> {
                Native.extractFromResourcePath("/assets/nullhack/libraries/libimgui-java64.so")
            }
            else -> throw IllegalStateException("Unsupported operating system")
        }
        tempFile.deleteOnExit()
        System.load(tempFile.absolutePath)
        return true
    }
}