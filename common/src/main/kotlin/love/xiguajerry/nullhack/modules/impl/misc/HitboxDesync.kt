package love.xiguajerry.nullhack.modules.impl.misc

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.TickEvent
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import kotlin.math.abs

object HitboxDesync : Module("Hitbox Desync", category = Category.MISC){
    init {
        nonNullHandler<TickEvent.Pre> {
            val f = player.direction
            val bb = player.boundingBox
            val center = bb.center
            val offset = f.unitVec3

            val fuck = merge(
                Vec3.atLowerCornerOf(BlockPos.containing(center))
                    .add(.5, 0.0, .5)
                    .add(offset.scale(0.20000996883537)),
                f
            )

            player.setPos(
                if (fuck.x == 0.0) player.x else fuck.x,
                player.y,
                if (fuck.z == 0.0) player.z else fuck.z
            )
            disable()
        }
    }

    private fun merge(a: Vec3, facing: Direction): Vec3 {
        return Vec3(
            a.x * abs(facing.unitVec3.x()),
            a.y * abs(facing.unitVec3.y()),
            a.z * abs(facing.unitVec3.z())
        )
    }
}