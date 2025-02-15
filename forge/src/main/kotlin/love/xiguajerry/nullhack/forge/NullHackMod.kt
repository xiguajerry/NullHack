package love.xiguajerry.nullhack.forge

import love.xiguajerry.nullhack.Metadata
import love.xiguajerry.nullhack.utils.sound.SoundPack
import net.minecraftforge.fml.common.Mod

@Mod(Metadata.ID)
class NullHackMod {
    init {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        SoundPack.registerSounds()
    }
}