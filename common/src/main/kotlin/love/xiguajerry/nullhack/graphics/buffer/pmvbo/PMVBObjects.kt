package love.xiguajerry.nullhack.graphics.buffer.pmvbo

import dev.luna5ama.kmogus.Arr
import dev.luna5ama.kmogus.asMutable
import love.xiguajerry.nullhack.RenderSystem
import love.xiguajerry.nullhack.event.impl.render.FenceSyncEvent
import love.xiguajerry.nullhack.graphics.GLHelper
import love.xiguajerry.nullhack.graphics.buffer.VertexAttribute
import love.xiguajerry.nullhack.graphics.buffer.VertexFormat
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.matrix.MatrixLayerStack
import love.xiguajerry.nullhack.graphics.shader.Shader
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL41.glProgramUniform1i
import org.lwjgl.opengl.GL45.*
import java.nio.ByteBuffer

/**
 * Requires OpenGL 4.5
 */
object PMVBObjects {
    fun onSync() {
        VertexMode.values.forEach {
            it.onSync()
        }
        FenceSyncEvent.post()
    }

    open class VertexMode(val format: VertexAttribute, val shader: Shader, sizeMb: Long = 64) {
        companion object {
            val values by lazy {
                listOf(
                    Pos2fColor, Pos2fColorTex, Pos2fColorTexArray,
                    Pos3fColor, Pos3fTex, Pos3fTexNV12, Pos3fTexYUV, Pos3fColorTex,
                    Universal
                )
            }
        }

        data object Pos2fColor : VertexMode(
            VertexFormat.Pos2fColor.attribute, Shader(
                "/assets/shader/general/Pos2fColor.vsh",
                "/assets/shader/general/Pos2fColor.fsh"
            )
        )

        data object Pos3fColor : VertexMode(
            VertexFormat.Pos3fColor.attribute, Shader(
                "/assets/shader/general/Pos3fColor.vsh",
                "/assets/shader/general/Pos3fColor.fsh"
            )
        )

        data object Pos2fColorTex : VertexMode(
            VertexFormat.Pos2fColorTex.attribute, Shader(
                "/assets/shader/general/Pos2fColorTex.vsh",
                "/assets/shader/general/Pos2fColorTex.fsh"
            ).apply {
                glProgramUniform1i(id, getUniformLocation("texture"), 0)
            }
        )

        data object Pos2fColorTexArray : VertexMode(
            VertexFormat.Pos2fColorTexArray.attribute, Shader(
                "/assets/shader/general/Pos2fColorTexArray.vsh",
                "/assets/shader/general/Pos2fColorTexArray.fsh"
            ).apply {
                glProgramUniform1i(id, getUniformLocation("texture"), 0)
            }
        ) {
            fun vertex(posX: Float, posY: Float, color: ColorRGBA, u: Float, v: Float, layer: Int) {
                val pointer = arr.ptr
                pointer[0] = posX
                pointer[4] = posY
                pointer[8] = color.rgba
                pointer[12] = u
                pointer[16] = v
                pointer[20] = layer.toFloat()
                arr += 24
                vertexSize++
            }

            fun vertex(posX: Float, posY: Float, u: Float, v: Float, layer: Int, color: ColorRGBA) {
                val pointer = arr.ptr
                pointer[0] = posX
                pointer[4] = posY
                pointer[8] = color.rgba
                pointer[12] = u
                pointer[16] = v
                pointer[20] = layer.toFloat()
                arr += 24
                vertexSize++
            }
        }

        data object Pos3fColorTex : VertexMode(
            VertexFormat.Pos3fColorTex.attribute, Shader(
                "/assets/shader/general/Pos3fColorTex.vsh",
                "/assets/shader/general/Pos3fColorTex.fsh"
            ).apply {
                glProgramUniform1i(id, getUniformLocation("texture"), 0)
            }
        )

        data object Pos3fTex : VertexMode(
            VertexFormat.Pos3fTex.attribute, Shader(
                "/assets/shader/general/Pos3fTex.vsh",
                "/assets/shader/general/Pos3fTex.fsh"
            ).apply {
                glProgramUniform1i(id, getUniformLocation("texture"), 0)
            }
        )

        data object Pos3fTexNV12 : VertexMode(
            VertexFormat.Pos3fTex.attribute, Shader(
                "/assets/shader/general/Pos3fTexNV12.vsh",
                "/assets/shader/general/Pos3fTexNV12.fsh"
            ).apply {
                glProgramUniform1i(id, getUniformLocation("tex_y"), 0)
                glProgramUniform1i(id, getUniformLocation("tex_uv"), 1)
            }
        )

        data object Pos3fTexYUV : VertexMode(
            VertexFormat.Pos3fTex.attribute, Shader(
                "/assets/shader/general/Pos3fTexYUV.vsh",
                "/assets/shader/general/Pos3fTexYUV.fsh"
            ).apply {
                glProgramUniform1i(id, getUniformLocation("tex_y"), 0)
                glProgramUniform1i(id, getUniformLocation("tex_u"), 1)
                glProgramUniform1i(id, getUniformLocation("tex_v"), 2)
            }
        )

        data object Universal : VertexMode(
            VertexFormat.Pos3fColorTex.attribute, Shader(
                "/assets/shader/general/UniversalDraw.vsh",
                "/assets/shader/general/UniversalDraw.fsh"
            ).apply {
                glProgramUniform1i(id, getUniformLocation("texture"), 0)
            }
        ) {
            fun rectSeparate(width: Float, height: Float, color: ColorRGBA) {
                rectSeparate(0f, 0f, width, height, color)
            }

            fun rectSeparate(startX: Float, startY: Float, endX: Float, endY: Float, color: ColorRGBA) {
                universal(endX, startY, color)
                universal(startX, startY, color)
                universal(endX, endY, color)
                universal(endX, endY, color)
                universal(startX, startY, color)
                universal(startX, endY, color)
            }

            fun universal(posX: Float, posY: Float, color: ColorRGBA) {
                val pointer = arr.ptr
                pointer[0] = posX
                pointer[4] = posY
                pointer[8] = 0f
                pointer[12] = color.rgba
                pointer[16] = -114514f
                pointer[20] = -114514f
                arr += 24
                vertexSize++
            }

            fun universal(posX: Float, posY: Float, u: Float, v: Float, color: ColorRGBA) {
                val pointer = arr.ptr
                pointer[0] = posX
                pointer[4] = posY
                pointer[8] = 0f
                pointer[12] = color.rgba
                pointer[16] = u
                pointer[20] = v
                arr += 24
                vertexSize++
            }

            fun universal(posX: Float, posY: Float, posZ: Float, color: ColorRGBA) {
                val pointer = arr.ptr
                pointer[0] = posX
                pointer[4] = posY
                pointer[8] = posZ
                pointer[12] = color.rgba
                pointer[16] = -114514f
                pointer[20] = -114514f
                arr += 24
                vertexSize++
            }

            fun universal(posX: Double, posY: Double, posZ: Double, color: ColorRGBA) {
                universal(posX.toFloat(), posY.toFloat(), posZ.toFloat(), color)
            }

            fun universal(posX: Float, posY: Float, posZ: Float, u: Float, v: Float, color: ColorRGBA) {
                val pointer = arr.ptr
                pointer[0] = posX
                pointer[4] = posY
                pointer[8] = posZ
                pointer[12] = color.rgba
                pointer[16] = u
                pointer[20] = v
                arr += 24
                vertexSize++
            }
        }

        private var matrixUniform = shader.getUniformLocation("matrix")

        private val vbo = glCreateBuffers().apply {
            glNamedBufferStorage(
                this,
                sizeMb * 1024L * 1024L,
                GL_MAP_WRITE_BIT or GL_MAP_PERSISTENT_BIT or GL_MAP_COHERENT_BIT
            )
        }
        protected val arr = Arr.wrap(
            glMapNamedBufferRange(
                vbo,
                0,
                sizeMb * 1024L * 1024L,
                GL_MAP_WRITE_BIT or GL_MAP_PERSISTENT_BIT or GL_MAP_COHERENT_BIT or GL_MAP_UNSYNCHRONIZED_BIT
            ) as ByteBuffer
        ).asMutable()

        private var drawOffset = 0
        private var sync = 0L

        protected var vertexSize = 0
        val vao = createVao(format, vbo)
        private val stride = format.stride

        private var lastMatrixCheckId = -1L

        fun updateMatrix(stack: MatrixLayerStack) {
            if (stack.checkID != lastMatrixCheckId) {
                glUniformMatrix4fv(matrixUniform, false, stack.matrixArray)
                lastMatrixCheckId = stack.checkID
            }
        }

        private fun end(stride: Int) {
            drawOffset = (arr.pos / stride).toInt()
        }

        fun onSync() {
            if (sync == 0L) {
                if (arr.pos >= arr.len / 2) {
                    sync = GL32C.glFenceSync(GL32.GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
                }
            } else if (IntArray(1).apply {
                    GL32C.glGetSynciv(
                        sync,
                        GL32.GL_SYNC_STATUS,
                        IntArray(1),
                        this
                    )
                }[0] == GL32.GL_SIGNALED) {
                GL32C.glDeleteSync(sync)
                sync = 0L
                arr.pos = 0L
                drawOffset = 0
            }
        }

        fun putVertex(posX: Float, posY: Float, u: Float, v: Float, color: ColorRGBA) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = color.rgba
            pointer[12] = u
            pointer[16] = v
            arr += 20
            vertexSize++
        }

        fun putVertex(posX: Float, posY: Float, posZ: Float, u: Float, v: Float) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = posZ
            pointer[12] = u
            pointer[16] = v
            arr += 20
            vertexSize++
        }

        fun putVertex(posX: Float, posY: Float, posZ: Float, u: Float, v: Float, color: ColorRGBA) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = posZ
            pointer[12] = color.rgba
            pointer[16] = u
            pointer[20] = v
            arr += 24
            vertexSize++
        }

        fun putVertex(posX: Float, posY: Float, posZ: Float, color: ColorRGBA) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = posZ
            pointer[12] = color.rgba
            arr += 16
            vertexSize++
        }

        fun putVertex(posX: Float, posY: Float, color: ColorRGBA) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = color.rgba
            arr += 12
            vertexSize++
        }

        open fun draw(vertexMode: VertexMode, shader: Shader, mode: Int) {
            if (vertexSize == 0) return
            shader.bind()
            vertexMode.updateMatrix(RenderSystem.matrixLayer)
            GLHelper.bindVertexArray(vertexMode.vao)
            glDrawArrays(mode, drawOffset, vertexSize)
            end(vertexMode.stride)
            vertexSize = 0
        }

        open fun multiDraw(vertexMode: VertexMode, shader: Shader, drawPoints: List<Pair<Int, Int>>, mode: Int) {

        }
    }

    fun createVao(vertexAttribute: VertexAttribute, vbo: Int): Int {
        val vaoID = glCreateVertexArrays()
        glVertexArrayVertexBuffer(vaoID, 0, vbo, 0L, vertexAttribute.stride)
        vertexAttribute.apply(vaoID, 0)
        return vaoID
    }

    fun <V : VertexMode> Int.draw(vertexMode: V, shader: Shader = vertexMode.shader, block: V.() -> Unit) {
        vertexMode.block()
        vertexMode.draw(vertexMode, shader, this)
    }

    fun <V : VertexMode> Int.multiDraw(vertexMode: V, shader: Shader = vertexMode.shader, block: V.() -> Unit) {
        vertexMode.block()
        vertexMode.draw(vertexMode, shader, this)
    }
}