package love.xiguajerry.nullhack.modules.impl.visual

import love.xiguajerry.nullhack.RS
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.render.CoreRender3DEvent
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings
import love.xiguajerry.nullhack.graphics.GLHelper
import love.xiguajerry.nullhack.graphics.VertexCache
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects.draw
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.matrix.rotatef
import love.xiguajerry.nullhack.graphics.matrix.scope
import love.xiguajerry.nullhack.graphics.matrix.translatef
import love.xiguajerry.nullhack.utils.extension.yaw
import love.xiguajerry.nullhack.utils.math.toRadian
import love.xiguajerry.nullhack.utils.math.vectors.Vec2d
import love.xiguajerry.nullhack.utils.math.vectors.Vec3f
import love.xiguajerry.nullhack.utils.world.EntityUtils
import org.lwjgl.opengl.GL11.*
import kotlin.math.sin

object BlueArchiveHalo : Module("Blue Archive Halo", category = Category.VISUAL, hidden = false) {
    val floatingSpeed by setting("Floating Speed", 0.5f)
    val color by setting("Color" ,ColorRGBA(0x78d1c7))
    val circle1 = VertexCache.createCircle(Vec2d(0.0, 0.0), 0.38)
    val circle2 = VertexCache.createCircle(Vec2d(0.0, 0.0), 0.2)

    init {
        nonNullHandler<CoreRender3DEvent> {
            RS.matrixLayer.scope {
                val interpolatedPos = EntityUtils.getInterpolatedPos(player, it.ticksDelta)
                GLHelper.lineSmooth = ClientSettings.useGlLineSmooth
                glLineWidth(2f)
                GL_LINE_STRIP.draw(PMVBObjects.VertexMode.Universal) {
                    circle1.forEach {
                        universal(interpolatedPos.x.toFloat() + it.x.toFloat(), interpolatedPos.y.toFloat() + 2.0f,
                            interpolatedPos.z.toFloat() + it.y.toFloat(), color)
                    }
                }
                val speedFactor = 1 / floatingSpeed
                val floatingDelta = 0.05f * sin(((System.currentTimeMillis() % (360 * speedFactor).toInt()) / speedFactor).toRadian())
                GL_LINE_STRIP.draw(PMVBObjects.VertexMode.Universal) {
                    circle2.forEach {
                        universal(interpolatedPos.x.toFloat() + it.x.toFloat(), interpolatedPos.y.toFloat() + 1.93f + floatingDelta,
                            interpolatedPos.z.toFloat() + it.y.toFloat(), color)
                    }
                }
                glLineWidth(3.5f)
                RS.matrixLayer.scope {
                    translatef(interpolatedPos.x.toFloat(), interpolatedPos.y.toFloat(), interpolatedPos.z.toFloat())
                    rotatef(-player.yaw, Vec3f(0f, 1f, 0f))
                    GL_LINE_STRIP.draw(PMVBObjects.VertexMode.Universal) {
                        universal(0.36f, 2.01f, 0f, color)
                        universal(0.43f, 2.01f, 0f, color)
                    }
                    GL_LINE_STRIP.draw(PMVBObjects.VertexMode.Universal) {
                        universal(-0.36f, 2.01f, 0f, color)
                        universal(-0.43f, 2.01f, 0f, color)
                    }
                    GL_LINE_STRIP.draw(PMVBObjects.VertexMode.Universal) {
                        universal(0f, 2.01f, 0.36f, color)
                        universal(0f, 2.01f, 0.43f, color)
                    }
                    GL_LINE_STRIP.draw(PMVBObjects.VertexMode.Universal) {
                        universal(0f, 2.01f, -0.36f, color)
                        universal(0f, 2.01f, -0.43f, color)
                    }
                }
                glLineWidth(1f)
            }
        }
    }
}