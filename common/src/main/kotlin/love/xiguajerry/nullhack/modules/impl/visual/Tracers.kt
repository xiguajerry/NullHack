package love.xiguajerry.nullhack.modules.impl.visual

import love.xiguajerry.nullhack.RenderSystem
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.render.CoreRender2DEvent
import love.xiguajerry.nullhack.manager.managers.EntityManager
import love.xiguajerry.nullhack.manager.managers.FriendManager
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.NonNullContext
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils
import love.xiguajerry.nullhack.graphics.buffer.Render3DUtils
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.utils.math.RotationUtils
import love.xiguajerry.nullhack.utils.math.vectors.HAlign.*
import love.xiguajerry.nullhack.utils.math.vectors.VAlign
import love.xiguajerry.nullhack.utils.math.vectors.VAlign.BOTTOM
import love.xiguajerry.nullhack.utils.math.vectors.VAlign.TOP
import love.xiguajerry.nullhack.utils.math.vectors.Vec2f
import love.xiguajerry.nullhack.utils.world.EntityUtils
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

object Tracers : Module("Tracers", category = Category.VISUAL) {
    private val players by setting("Players", true)
    private val items by setting("Items", true)
    private val mobs by setting("Mobs", false)
    private val passive by setting("Passive", false)
    private val neutral by setting("Neutral", false)
    private val hostile by setting("Hostile", false)
    private val hAlign by setting("HAlign", CENTER)
    private val vAlign by setting("VAlign", VAlign.CENTER)
    private val thickness by setting("Thickness", 1.0f, 0.0f..5.0f, 0.1f)
    private val color by setting("Color", ColorRGBA.WHITE)
    private val friendColor by setting("Friend Color", ColorRGBA.BLUE)

    init {
        nonNullHandler<CoreRender2DEvent> {
            EntityManager.entity.forEach { entity ->
                val interpolatedPos = EntityUtils.getInterpolatedPos(entity, it.ticksDelta)
                when {
                    entity is Player && players -> {
                        if (entity != player) {
                            drawLineTo(
                                interpolatedPos,
                                if (FriendManager.isFriend(player.displayName?.string ?: player.name.string))
                                    friendColor else color
                            )
                        }
                    }
                    entity is ItemEntity && items -> {
                        drawLineTo(interpolatedPos, color)
                    }
                    entity is LivingEntity -> {
                        if (EntityUtils.mobTypeSettings(entity, mobs, passive, neutral, hostile)) {
                            drawLineTo(interpolatedPos, color)
                        }
                    }
                }
            }
        }
    }

    context (NonNullContext)
    private fun drawLineTo(pos: Vec3, color: ColorRGBA) {
        val cameraPos = mc.entityRenderDispatcher.camera.position
        val x = when (hAlign) {
            LEFT -> 0.0
            CENTER -> RenderSystem.scaledWidth / 2
            RIGHT -> RenderSystem.scaledWidth
        }
        val y = when (vAlign) {
            TOP -> 0.0
            VAlign.CENTER -> RenderSystem.scaledHeight / 2
            BOTTOM -> RenderSystem.scaledHeight
        }
        if (RotationUtils.getRotationDiff(
                Vec2f(mc.entityRenderDispatcher.camera.yRot, mc.entityRenderDispatcher.camera.xRot),
                RotationUtils.getRotationTo(cameraPos, pos)) < mc.options.fov().get()) {
            val screenPos = Render3DUtils.worldToScreen(pos)
            Render2DUtils.drawLine(screenPos.x, screenPos.y, x, y, thickness, color)
        }
    }

    private enum class Position : Displayable {
        START, CENTER, END
    }
}