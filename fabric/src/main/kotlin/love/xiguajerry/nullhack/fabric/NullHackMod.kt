package love.xiguajerry.nullhack.fabric

import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.utils.sound.SoundPack
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader

class NullHackMod : ModInitializer {
    override fun onInitialize() {
        NullHackMod.FOLDER = (FabricLoader.getInstance().gameDir.resolve(NullHackMod.ID).toFile())
        SoundPack.registerSounds()
    }
}