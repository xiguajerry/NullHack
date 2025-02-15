package love.xiguajerry.nullhack.command

import love.xiguajerry.nullhack.command.args.AbstractArg
import love.xiguajerry.nullhack.command.args.AutoComplete
import love.xiguajerry.nullhack.command.args.DynamicPrefixMatch
import love.xiguajerry.nullhack.command.args.StaticPrefixMatch
import love.xiguajerry.nullhack.manager.managers.ModuleManager
import love.xiguajerry.nullhack.modules.AbstractModule
import love.xiguajerry.nullhack.utils.MinecraftWrapper
import love.xiguajerry.nullhack.utils.delegates.CachedValueN
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block

class ModuleArg(
    override val name: String
) : AbstractArg<AbstractModule>(), AutoComplete by DynamicPrefixMatch(::allAlias) {
    override suspend fun convertToType(string: String?): AbstractModule? {
        return string?.let { ModuleManager.getModuleByName(it) }
    }

    private companion object {
        val allAlias by CachedValueN(5000L) {
            ModuleManager.modules.asSequence()
                .flatMap {
                    sequence {
                        yield(it.internalName)
                        it.alias.forEach {
                            yield(it.toString())
                        }
                    }
                }
                .sorted()
                .toList()
        }
    }

}

//class HudElementArg(
//    override val name: String
//) : AbstractArg<AbstractHudElement>(), AutoComplete by DynamicPrefixMatch(::allAlias) {
//    override suspend fun convertToType(string: String?): AbstractHudElement? {
//        return GuiManager.getHudElementOrNull(string)
//    }
//
//    private companion object {
//        val allAlias by CachedValueN(5000L) {
//            GuiManager.hudElements.asSequence()
//                .flatMap {
//                    sequence {
//                        yield(it.internalName)
//                        it.alias.forEach {
//                            yield(it.toString())
//                        }
//                    }
//                }
//                .sorted()
//                .toList()
//        }
//    }
//}

class BlockPosArg(
    override val name: String
) : AbstractArg<BlockPos>(), AutoComplete by DynamicPrefixMatch(::playerPosString) {

    override suspend fun convertToType(string: String?): BlockPos? {
        if (string == null) return null

        val splitInts = string.split(',').mapNotNull { it.toIntOrNull() }
        if (splitInts.size != 3) return null

        return BlockPos(splitInts[0], splitInts[1], splitInts[2])
    }

    private companion object {
        val playerPosString: List<String>?
            get() = MinecraftWrapper.mc.player?.blockPosition()?.let { listOf("${it.x},${it.y},${it.z}") }
    }

}

class BlockArg(
    override val name: String
) : AbstractArg<Block>(), AutoComplete by StaticPrefixMatch(allBlockNames) {

    override suspend fun convertToType(string: String?): Block? {
        if (string == null) return null

        return BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(string)!!).get().value()
    }

    private companion object {
        val allBlockNames = ArrayList<String>().apply {
            BuiltInRegistries.BLOCK.keySet().forEach {
                add(it.toString())
            }
            sort()
        }
    }
}

//class ItemArg(
//    override val name: String
//) : AbstractArg<Item>(), AutoComplete by StaticPrefixMatch(allItemNames) {
//
//    override suspend fun convertToType(string: String?): Item? {
//        if (string == null) return null
//        return Item.getByNameOrId(string)
//    }
//
//    private companion object {
//        val allItemNames = ArrayList<String>().run {
//            Item.REGISTRY.keys.forEach {
//                add(it.toString())
//                add(it.path)
//            }
//            sorted()
//        }
//    }
//
//}