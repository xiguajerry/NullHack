package love.xiguajerry.nullhack.neoforge

import love.xiguajerry.nullhack.platform.IModLoaderPlatform
import net.neoforged.fml.ModList
import kotlin.jvm.optionals.getOrNull

class NeoforgePlatform : IModLoaderPlatform {
    override fun getModVersion(mod: String): String? {
        return ModList.get()?.getModContainerById(mod)?.getOrNull()?.modInfo?.version?.qualifier
    }
}