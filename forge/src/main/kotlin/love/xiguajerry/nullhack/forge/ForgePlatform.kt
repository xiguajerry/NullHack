package love.xiguajerry.nullhack.forge

import love.xiguajerry.nullhack.platform.IModLoaderPlatform
import net.minecraftforge.fml.ModList
import kotlin.jvm.optionals.getOrNull

class ForgePlatform : IModLoaderPlatform {
    override fun getModVersion(mod: String): String? {
        return ModList.get()?.getModContainerById(mod)?.getOrNull()?.modInfo?.version?.qualifier
    }
}