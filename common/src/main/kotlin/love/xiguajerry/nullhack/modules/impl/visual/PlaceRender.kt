package love.xiguajerry.nullhack.modules.impl.visual

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.TickEvent
import love.xiguajerry.nullhack.event.impl.render.Render3DEvent
import love.xiguajerry.nullhack.event.impl.world.PlaceBlockEvent
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.ChatUtils
import love.xiguajerry.nullhack.graphics.animations.BlockEasingRender
import love.xiguajerry.nullhack.graphics.buffer.Render3DUtils
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.utils.timing.TickTimer
import love.xiguajerry.nullhack.utils.timing.TimeUnit
import net.minecraft.core.BlockPos

object PlaceRender : Module("Place Render", category = Category.VISUAL) {
    private val delay by setting("Show Time S", 1.0f, 0.1f..2.0f, 0.1f)
    private val color by setting("Color", ColorRGBA.WHITE.alpha(128))
    private val lineColor by setting("Line Color", ColorRGBA.WHITE)
    private val lineWidth by setting("Line Width", 1.0f, 0.1f..4.0f)
    private val debug by setting("debug", false)
    private val animation = BlockEasingRender(BlockPos.ZERO, 500f, 500f)
    private val timer = TickTimer()

    init {
        nonNullHandler<TickEvent.Post> {
            if (timer.tickAndReset(delay.toInt(), TimeUnit.SECONDS)) {
                animation.end()
            }
        }

        nonNullHandler<PlaceBlockEvent> {
            val renderPos = it.getBlockPos()
            timer.reset()
            animation.begin()
            animation.updatePos(renderPos)
            if (debug){
                ChatUtils.sendRawMessage(renderPos.toString())
            }
        }

        nonNullHandler<Render3DEvent> {
            val (box, _) = animation.updateVec3Box()
            Render3DUtils.drawBox(box, color)
            Render3DUtils.drawBoxOutline(box, lineWidth, lineColor)
        }
    }
}