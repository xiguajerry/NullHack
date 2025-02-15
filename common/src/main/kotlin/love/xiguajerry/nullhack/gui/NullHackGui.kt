package love.xiguajerry.nullhack.gui

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.RenderSystem.matrixLayer
import love.xiguajerry.nullhack.RenderSystem.modelView
import love.xiguajerry.nullhack.RenderSystem.projection
import love.xiguajerry.nullhack.event.api.AlwaysListening
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.render.Render2DEvent
import love.xiguajerry.nullhack.gui.components.CommonSettingComponent
import love.xiguajerry.nullhack.gui.components.ModuleComponent
import love.xiguajerry.nullhack.gui.components.Panel
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.modules.AbstractModule
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings
import love.xiguajerry.nullhack.utils.MinecraftWrapper.mc
import love.xiguajerry.nullhack.graphics.GLHelper
import love.xiguajerry.nullhack.graphics.animations.AnimationFlag
import love.xiguajerry.nullhack.graphics.animations.Easing
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects
import love.xiguajerry.nullhack.graphics.font.TextComponent
import love.xiguajerry.nullhack.graphics.imgui.ImGuiScreen
import love.xiguajerry.nullhack.graphics.matrix.scope
import love.xiguajerry.nullhack.graphics.matrix.translatef
import love.xiguajerry.nullhack.graphics.texture.MipmapTexture
import love.xiguajerry.nullhack.graphics.texture.drawTexture
import love.xiguajerry.nullhack.utils.input.CursorStyle
import love.xiguajerry.nullhack.utils.math.vectors.HAlign
import love.xiguajerry.nullhack.utils.math.vectors.Vec2d
import love.xiguajerry.nullhack.utils.state.FrameFloat
import net.minecraft.client.gui.GuiGraphics
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.glBlendFuncSeparate
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sign
import love.xiguajerry.nullhack.RenderSystem as RS

abstract class NullHackGui<G : NullHackGui<G>>(name: String) : ImGuiScreen(name), AlwaysListening {
    protected open val self: G
        get() = this as G
    protected val transEasing = AnimationFlag(Easing.OUT_CUBIC, 800F)
    protected val alpha = AnimationFlag(Easing.LINEAR, 700f)
    protected val pictureAnim = AnimationFlag(Easing.OUT_CUBIC, 600f)
    val panels = mutableListOf<Panel<G>>()
    protected val maxHeight by FrameFloat { panels.maxOf { it.y + it.height } }
    abstract val outline: Boolean
    protected abstract val settingModule: AbstractModule
    protected abstract val clearBuffer: Boolean
    protected abstract val bottomAlpha: Int
    protected abstract val anime: MipmapTexture
    protected abstract val animeHeight: Float
    protected abstract val animeXOffset: Float
    protected abstract val mouseScrollSpeed: Int
    protected abstract val pauseGame: Boolean
    protected abstract val categories: List<Category>

    init {
        setup()

        nonNullHandler<Render2DEvent> {
            val width = RS.scaledWidthF
            val height = RS.scaledHeightF
            val mouseX = RS.mouseX * width / mc.window.width
            val mouseY = RS.mouseY * height / mc.window.height
            if (mc.screen == null) {
                val trans = transEasing.getAndUpdate(-maxHeight * 2)
                if ((trans - (-maxHeight * 2)).absoluteValue < 0.01f)
                    return@nonNullHandler
                render(it.context, mouseX.toInt(), mouseY.toInt(), it.ticksDelta)
            }
        }
    }

    override fun isPauseScreen(): Boolean = pauseGame

    fun reloadPanel() {
        setup()
        panels.forEach { it.resetPanel() }
    }

    override fun onClose() {
        ImGui.setMouseCursor(CursorStyle.DEFAULT.imgui)
        GLFW.glfwSetCursor(mc.window.window, CursorStyle.DEFAULT.cursor)
        super.onClose()
    }

    protected open fun setup() {
        panels.clear()
        val width = 120f
        val y = 5f
        val gap = 4f
        var startX = 5f
        categories.forEach { category ->
            panels.add(
                Panel(startX, y, self, width, category).also { it.extended = true })
            startX += (width + gap)
        }
    }

    final override fun added() {
        pictureAnim.forceUpdate(RS.scaledWidthF, RS.scaledWidthF)
        transEasing.forceUpdate(
            -maxHeight * 2,
            -maxHeight * 2
        )
        alpha.forceUpdate(0f, 0f)
        panels.forEach { it.init() }
    }

    final override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        if (mc.screen == this) {
            RS.preRender()

            GLHelper.blend = true
            glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
            GLHelper.cull = false
            GLHelper.depth = false

            matrixLayer.scope {
                modelView.set(Matrix4f(RenderSystem.getModelViewMatrix()))
                projection.set(Matrix4f(RenderSystem.getProjectionMatrix()))

                render(mouseX, mouseY)
            }

            matrixLayer.scope {
                renderImGui()
            }

            RS.postRender()

            GlStateManager._enableBlend()
            GlStateManager._disableCull()
            GlStateManager._disableDepthTest()
            RenderSystem.defaultBlendFunc()
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        } else if (mc.screen == null) render(mouseX, mouseY)
    }

    protected open fun renderImGui() {
//        startFrame()
//        preProcess()
//        process()
//        postProcess()
//        endFrame()
    }

    protected open fun render(mouseX: Int, mouseY: Int) {
        if (!ImGui.getIO().wantCaptureMouse) {
            ImGui.getIO().addConfigFlags(ImGuiConfigFlags.NoMouseCursorChange)
        } else ImGui.getIO().removeConfigFlags(ImGuiConfigFlags.NoMouseCursorChange)

        try {
            val trans = transEasing.getAndUpdate(
                if (mc.screen != self) -maxHeight * 2
                else 0f
            )
            ClientSettings.backgroundType.draw((trans - (-maxHeight * 2)) / (maxHeight * 2))

            val alpha = alpha.getAndUpdate(
                if (mc.screen == self) bottomAlpha.toFloat() else 0f
            ).toInt()
            GLHelper.useProgram(PMVBObjects.VertexMode.Universal.shader.id, true)
            GLHelper.bindVertexArray(PMVBObjects.VertexMode.Universal.vao, true)
            matrixLayer.scope {
                Render2DUtils.drawGradientRect0(
                    0.0f, 0.0f,
                    RS.scaledWidthF, RS.scaledHeightF,
                    GuiManager.getColor().alpha(0),
                    GuiManager.getColor().alpha(0),
                    GuiManager.getColor().alpha(alpha),
                    GuiManager.getColor().alpha(alpha)
                )

                val picHeight = animeHeight
                val picWidth = anime.width * (animeHeight / anime.height)
                val x = pictureAnim.getAndUpdate(
                    if (mc.screen == self)
                        RS.scaledWidthF - picWidth - animeXOffset
                    else RS.scaledWidthF
                )
                val y = RS.scaledHeightF - picHeight
                anime.drawTexture(
                    x, y,
                    x + picWidth,
                    y + picHeight
                )

                translatef(0f, trans, 0f)

                panels.reversed().forEach {
                    it.render(mouseX, mouseY)
                }

                if (!ImGui.getIO().wantCaptureMouse) {
                    panels.forEach {
                        if (it.drawTooltip(mouseX, mouseY))
                            return@scope
                    }
                }
            }

            GLHelper.depth = false
            val text = TextComponent()
            val textAlpha = if (mc.screen == self) 255 else min(alpha, 255)
            val watermark = "${NullHackMod.NAME} ${NullHackMod.TYPE}, "
            val copyright = "All rights reserved"
            text.addLine(watermark + copyright)
            text.draw(
                Vec2d(RS.scaledWidth, RS.scaledHeight - text.getHeight(0)),
                alpha = textAlpha / 255f, horizontalAlign = HAlign.RIGHT
            )
        } catch (e: Exception) {
            NullHackMod.LOGGER.error("Exception was throw when rendering Gui:", e)
            e.printStackTrace()
        }
    }

    override fun process() {
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (ImGui.getIO().wantCaptureMouse) return true
        panels.forEach { it.mouseReleased(mouseX.toInt(), mouseY.toInt(), button) }
        return false
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        if (ImGui.getIO().wantCaptureMouse) return
        if (!handleMouseInput(mouseX.toInt(), mouseY.toInt())) {
            ImGui.setMouseCursor(CursorStyle.DEFAULT.imgui)
            GLFW.glfwSetCursor(mc.window.window, CursorStyle.DEFAULT.cursor)
        }
    }

    protected fun handleMouseInput(mouseX: Int, mouseY: Int): Boolean {
        val modules = mutableListOf<ModuleComponent<G>>()
        panels.forEach {  panel ->
            if (panel.handleMouseInput(mouseX, mouseY)) return true
            if (!panel.extended) return@forEach
            modules.addAll(panel.children)
        }
        val settings = mutableListOf<CommonSettingComponent<*, G>>()
        modules.forEach { module ->
            if (module.handleMouseInput(mouseX, mouseY)) return true
            if (!module.extended) return@forEach
            settings.addAll(module.children)
        }
        settings.forEach { setting ->
            if (setting.handleMouseInput(mouseX, mouseY)) return true
        }

        return false
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (ImGui.getIO().wantCaptureMouse) return true
        val modules = mutableListOf<ModuleComponent<G>>()
        panels.forEach { panel ->
            if (panel.clicked(mouseX.toInt(), mouseY.toInt(), button)) return true
            if (!panel.extended) return@forEach
            modules.addAll(panel.children)
        }
        val settings = mutableListOf<CommonSettingComponent<*, G>>()
        modules.forEach { module ->
            if (module.clicked(mouseX.toInt(), mouseY.toInt(), button)) return true
            if (!module.extended) return@forEach
            settings.addAll(module.children)
        }
        settings.forEach { setting ->
            if (setting.clicked(mouseX.toInt(), mouseY.toInt(), button)) return true
        }
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            settingModule.disable()
            onClose()
            return true
        }
        for (panel in panels) {
            panel.onKeyTyped('\u0000', keyCode)
        }
        return true
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return true
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        for (panel in panels) {
            panel.onKeyTyped(chr, GLFW.GLFW_KEY_UNKNOWN)
        }
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        if (ImGui.getIO().wantCaptureMouse) return true
        val v = (mouseScrollSpeed * verticalAmount.sign).toFloat()
        val h = (mouseScrollSpeed * horizontalAmount.sign).toFloat()
        panels.forEach { it.move(it.x, it.y + v) }
//        if (verticalAmount < 0) {
//            panels.forEach { it.move(it.x, it.y - mouseScrollSpeed) }
//        } else if (verticalAmount > 0) {
//            panels.forEach { it.move(it.x, it.y + mouseScrollSpeed) }
//        }
        panels.forEach { it.move(it.x + h, it.y) }
//        if (horizontalAmount < 0) {
//            panels.forEach { it.move(it.x - mouseScrollSpeed, it.y) }
//        } else if (horizontalAmount > 0) {
//            panels.forEach { it.move(it.x + mouseScrollSpeed, it.y) }
//        }
        return true
    }
}