package love.xiguajerry.nullhack.graphics

import com.mojang.blaze3d.systems.RenderSystem
import love.xiguajerry.nullhack.utils.MinecraftWrapper
import love.xiguajerry.nullhack.utils.NonNullContext
import love.xiguajerry.nullhack.graphics.buffer.Render3DUtils
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.utils.world.DirectionMask
import net.minecraft.world.entity.Entity
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB

class ESPRenderer {
    private var toRender0: MutableList<Info> = ArrayList()

    val toRender: List<Info>
        get() = toRender0

    var aFilled = 0
    var aOutline = 0
    var aTracer = 0
    var thickness = 2f
    var through = true
    var tracerOffset = 50

    val size: Int
        get() = toRender0.size

    fun add(entity: Entity, color: ColorRGBA) {
        add(entity, color, DirectionMask.ALL)
    }

    fun add(entity: Entity, color: ColorRGBA, sides: Int) {
        val partialTicks = 1.0f - MinecraftWrapper.mc.deltaTracker.getGameTimeDeltaPartialTick(true)
        val x = (entity.xo - entity.x) * partialTicks
        val y = (entity.yo - entity.y) * partialTicks
        val z = (entity.zo - entity.z) * partialTicks
        val interpolatedBox = entity.boundingBox.move(x, y, z)
        add(interpolatedBox, color, sides)
    }

    fun add(pos: BlockPos, color: ColorRGBA) {
        add(pos, color, DirectionMask.ALL)
    }

    fun add(pos: BlockPos, color: ColorRGBA, sides: Int) {
        add(AABB(pos), color, sides)
    }

    fun add(box: AABB, color: ColorRGBA) {
        add(box, color, DirectionMask.ALL)
    }

    fun add(box: AABB, color: ColorRGBA, sides: Int) {
        add(Info(box, color, sides))
    }

    fun add(info: Info) {
        toRender0.add(info)
    }

    fun replaceAll(list: MutableList<Info>) {
        toRender0 = list
    }

    fun clear() {
        toRender0.clear()
    }

    context(NonNullContext)
    fun render(clear: Boolean) {
        val filled = aFilled != 0
        val outline = aOutline != 0
        val tracer = aTracer != 0
        if (toRender0.isEmpty() || (!filled && !outline && !tracer)) return

        if (through) GLHelper.depth = false
        RenderSystem.lineWidth(thickness)

        if (filled) {
            for ((box, color, sides) in toRender0) {
                val a = (aFilled * (color.a / 255.0f)).toInt()
                Render3DUtils.drawBox(box, color.alpha(a))
            }
//            RenderUtils3D.draw(GL_QUADS)
        }

        if (outline || tracer) {
            if (outline) {
                for ((box, color, _) in toRender0) {
                    val a = (aOutline * (color.a / 255.0f)).toInt()
                    Render3DUtils.drawBoxOutline(box, 1f, color.alpha(a))
                }
            }
//            if (tracer) {
//                for ((box, color, _) in toRender0) {
//                    val a = (aTracer * (color.a / 255.0f)).toInt()
//                    val offset = (tracerOffset - 50) / 100.0 * (box.maxY - box.minY)
//                    val offsetBox = box.center.add(0.0, offset, 0.0)
//                    Render3DUtils.drawLineTo(offsetBox, color.alpha(a))
//                }
//            }

//            RenderUtils3D.draw(GL_LINES)
        }

        if (clear) clear()
        GLHelper.depth = true
    }

    private enum class Type {
        FILLED, OUTLINE, TRACER
    }

    data class Info(val box: AABB, val color: ColorRGBA, val sides: Int) {
        constructor(box: AABB) : this(box, ColorRGBA(255, 255, 255), DirectionMask.ALL)
        constructor(box: AABB, color: ColorRGBA) : this(box, color, DirectionMask.ALL)
        constructor(pos: BlockPos) : this(AABB(pos), ColorRGBA(255, 255, 255), DirectionMask.ALL)
        constructor(pos: BlockPos, color: ColorRGBA) : this(AABB(pos), color, DirectionMask.ALL)
        constructor(pos: BlockPos, color: ColorRGBA, sides: Int) : this(AABB(pos), color, sides)
    }
}