package love.xiguajerry.nullhack.utils.extension

import net.minecraft.client.Minecraft

val Minecraft.profiler get() = metricsRecorder.profiler