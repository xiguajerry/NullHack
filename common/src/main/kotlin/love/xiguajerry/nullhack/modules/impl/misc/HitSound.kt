package love.xiguajerry.nullhack.modules.impl.misc

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.player.AttackEntityEvent
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.modules.impl.misc.HitSound.Mode.*
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.NonNullContext
import love.xiguajerry.nullhack.utils.sound.SoundPack
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.boss.enderdragon.EndCrystal

object HitSound  : Module(
    "Hit Sound",
    "HitSound",
    Category.MISC
) {
    private val mode by setting("Sound", MOAN)
    private val volume by setting("Volume", 1f, 0.1f..10f)
    private val pitch by setting("Pitch", 1f, 0.1f..10f)

    init {
        nonNullHandler<AttackEntityEvent> {
            if (it.entity is EndCrystal)return@nonNullHandler
            when(mode){
                UWU ->  playSound(SoundPack.UWU_SOUNDEVENT)
                MOAN -> {
                    val i = random(0, 5)
                    when(i){
                        1-> playSound(SoundPack.MOAN1_SOUNDEVENT)
                        2-> playSound(SoundPack.MOAN2_SOUNDEVENT)
                        3-> playSound(SoundPack.MOAN3_SOUNDEVENT)
                        4-> playSound(SoundPack.MOAN4_SOUNDEVENT)
                        5-> playSound(SoundPack.MOAN5_SOUNDEVENT)
                    }
                }
                SKEET -> playSound(SoundPack.SKEET_SOUNDEVENT)
                KEYBOARD -> playSound(SoundPack.KEYPRESS_SOUNDEVENT)
            }

        }
    }

    context(NonNullContext)
    private fun playSound(s: SoundEvent) {
        world.playSound(
            player,
            player.blockPosition(),
            s,
            SoundSource.BLOCKS,
            volume,
            pitch
        )
    }

    fun random(min: Int, max: Int): Int {
        return (Math.random() * (max - min) + min).toInt()
    }

    enum class Mode : Displayable {
        UWU, MOAN, SKEET, KEYBOARD
    }
}