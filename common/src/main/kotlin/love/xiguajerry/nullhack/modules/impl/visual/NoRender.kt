package love.xiguajerry.nullhack.modules.impl.visual

import love.xiguajerry.nullhack.event.api.handler
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.PacketEvent
import love.xiguajerry.nullhack.event.impl.render.ParticleEvent
import love.xiguajerry.nullhack.event.impl.render.Render3DEvent
import love.xiguajerry.nullhack.manager.managers.EntityManager
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import net.minecraft.client.particle.*
import net.minecraft.world.entity.Entity
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.world.entity.projectile.Arrow
import net.minecraft.world.entity.projectile.ThrownEgg
import net.minecraft.world.entity.projectile.ThrownExperienceBottle
import net.minecraft.world.entity.projectile.ThrownPotion

object NoRender : Module("No Render", category = Category.VISUAL) {
    val potions by setting("Potions", true)
    val xp by setting("XP", true)
    val arrows by setting("Arrows", true)
    val eggs by setting("Eggs", true)
    val armor by setting("Armor", true)
    val hurtCam by setting("HurtCam", true)
    val fireOverlay by setting("FireOverlay", true)
    val waterOverlay by setting("WaterOverlay", true)
    val blockOverlay by setting("BlockOverlay", true)
    val portal by setting("Portal", true)
    val totem by setting("Totem", true)
    val nausea by setting("Nausea", true)
    val blindness by setting("Blindness", true)
    val fog by setting("Fog", true)
    val darkness by setting("Darkness", true)
    val fireEntity by setting("EntityFire", true)
    val antiTitle by setting("Title", true)
    val effect by setting("Effect", true)
    val elderGuardian by setting("Guardian", true)
    val explosions by setting("Explosions", true)
    val campFire by setting("CampFire", true)
    val fireworks by setting("Fireworks", true)

    init {
        handler<PacketEvent.Receive> {
            if (it.packet is ClientboundSetTitleTextPacket && antiTitle) {
                it.cancel()
            }
        }

        nonNullHandler<Render3DEvent> {
            for (ent in EntityManager.entity) {
                when (ent) {
                    is ThrownPotion -> if (potions) world.removeEntity(ent.id, Entity.RemovalReason.KILLED)
                    is ThrownExperienceBottle -> if (xp) world.removeEntity(ent.id, Entity.RemovalReason.KILLED)
                    is Arrow -> if (arrows) world.removeEntity(ent.id, Entity.RemovalReason.KILLED)
                    is ThrownEgg -> if (eggs) world.removeEntity(ent.id, Entity.RemovalReason.KILLED)
                }
            }
        }

        nonNullHandler<ParticleEvent.AddParticle> {
            when (it.particle) {
                is MobAppearanceParticle -> if (elderGuardian) it.cancel()
                is HugeExplosionParticle -> if (explosions) it.cancel()
                is CampfireSmokeParticle -> if (campFire) it.cancel()
                is FireworkParticles.Starter -> if (fireworks) it.cancel()
                is FireworkParticles.OverlayParticle -> if (fireworks) it.cancel()
                is SpellParticle -> if (effect) it.cancel()
            }
        }
    }
}