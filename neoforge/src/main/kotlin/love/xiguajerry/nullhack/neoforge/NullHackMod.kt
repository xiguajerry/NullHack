package love.xiguajerry.nullhack.neoforge

import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.NullHackMod.LOGGER
import love.xiguajerry.nullhack.utils.Profiler
import love.xiguajerry.nullhack.utils.sound.SoundPack
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.RegisterEvent

@Mod("nullhack")
class NullHackMod {
    init {
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        NullHackMod.LOGGER.info("Starting up mod")
    }

    @SubscribeEvent
    fun register(event: RegisterEvent) {
        event.register(
            // This is the registry key of the registry.
            // Get these from BuiltInRegistries for vanilla registries,
            // or from NeoForgeRegistries.Keys for NeoForge registries.
            Registries.SOUND_EVENT
        ) { registry ->
            LOGGER.info("Initializing Sounds")
            Profiler.BootstrapProfiler("Initialize Sound") {
                SoundPack.registerSounds()
            }
        }
    }
}