package love.xiguajerry.nullhack.modules.impl.visual

import com.mojang.blaze3d.platform.GlStateManager
import love.xiguajerry.nullhack.RenderSystem
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.MinecraftWrapper
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects.draw
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.matrix.getFloatArray
import love.xiguajerry.nullhack.graphics.shader.Shader
import love.xiguajerry.nullhack.graphics.shader.useShader
import love.xiguajerry.nullhack.utils.math.vectors.Vec2i
import net.minecraft.world.entity.Entity
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*

object Shaders : Module("Shaders", category = Category.VISUAL) {

    fun shouldRender(entity: Entity): Boolean {
        return isEnabled && entity != MinecraftWrapper.mc.player
    }

    object OutlineShader : Shader("/assets/shader/effect/outline.vsh", "/assets/shader/effect/outline.fsh") {
        private var lastResolution = Vec2i.ZERO
        private val projMat = Matrix4f()

        fun updateUniform(resolution: Vec2i) {
            useShader {
//                if (resolution != lastResolution) {
//                    projMat.identity()
                    projMat.setOrtho(0f, resolution.x.toFloat(), 0f, resolution.y.toFloat(), 0.1f, 1000f)
//                }

//                println(Vector4f(resolution.x.toFloat(), resolution.y.toFloat(), 0f, 1f).mul(projMat))
                glUniformMatrix4fv(0, false, projMat.getFloatArray())
                glUniform2f(1, resolution.x.toFloat(), resolution.y.toFloat())
                glUniform2f(2, resolution.x.toFloat(), resolution.y.toFloat())
            }
        }
    }
}