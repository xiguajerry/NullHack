package love.xiguajerry.nullhack.utils.sound

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent

object SoundPack {
    val KEYPRESS_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:keypress")
    var KEYPRESS_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(KEYPRESS_SOUND)
    val KEYRELEASE_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:keyrelease")
    var KEYRELEASE_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(KEYRELEASE_SOUND)
    val UWU_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:uwu")
    var UWU_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(UWU_SOUND)
    val ENABLE_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:enable")
    var ENABLE_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(ENABLE_SOUND)
    val DISABLE_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:disable")
    var DISABLE_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(DISABLE_SOUND)
    val MOAN1_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:moan1")
    var MOAN1_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(MOAN1_SOUND)
    val MOAN2_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:moan2")
    var MOAN2_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(MOAN2_SOUND)
    val MOAN3_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:moan3")
    var MOAN3_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(MOAN3_SOUND)
    val MOAN4_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:moan4")
    var MOAN4_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(MOAN4_SOUND)
    val MOAN5_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:moan5")
    var MOAN5_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(MOAN5_SOUND)
    val SKEET_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:skeet")
    var SKEET_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(SKEET_SOUND)
    val ORTHODOX_SOUND: ResourceLocation = ResourceLocation.parse("nullhack:orthodox")
    var ORTHODOX_SOUNDEVENT: SoundEvent = SoundEvent.createVariableRangeEvent(ORTHODOX_SOUND)

    fun registerSounds() {
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, KEYPRESS_SOUND, KEYPRESS_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, KEYRELEASE_SOUND, KEYRELEASE_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, ENABLE_SOUND, ENABLE_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, DISABLE_SOUND, DISABLE_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, MOAN1_SOUND, MOAN1_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, MOAN2_SOUND, MOAN2_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, MOAN3_SOUND, MOAN3_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, MOAN4_SOUND, MOAN4_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, MOAN5_SOUND, MOAN5_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, UWU_SOUND, UWU_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, SKEET_SOUND, SKEET_SOUNDEVENT
        )
        Registry.register(
            BuiltInRegistries.SOUND_EVENT, ORTHODOX_SOUND, ORTHODOX_SOUNDEVENT
        )
    }
}