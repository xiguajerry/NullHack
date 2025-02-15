package love.xiguajerry.nullhack.utils.extension

import love.xiguajerry.nullhack.utils.MinecraftWrapper
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.enchantment.Enchantment

val ResourceKey<Enchantment>.entry get() = MinecraftWrapper.mc.level!!.registryAccess().get(this).get()