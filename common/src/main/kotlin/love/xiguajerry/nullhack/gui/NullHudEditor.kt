package love.xiguajerry.nullhack.gui

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.RenderSystem.matrixLayer
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.render.Render2DEvent
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.modules.AbstractModule
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.impl.client.ClickGui
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings
import love.xiguajerry.nullhack.modules.impl.client.HudEditor
import love.xiguajerry.nullhack.utils.MinecraftWrapper.mc
import love.xiguajerry.nullhack.utils.delegates.CachedValueN
import love.xiguajerry.nullhack.graphics.GLHelper
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects
import love.xiguajerry.nullhack.graphics.font.TextComponent
import love.xiguajerry.nullhack.graphics.matrix.scope
import love.xiguajerry.nullhack.graphics.matrix.translatef
import love.xiguajerry.nullhack.graphics.texture.MipmapTexture
import love.xiguajerry.nullhack.graphics.texture.drawTexture
import love.xiguajerry.nullhack.utils.input.CursorStyle
import love.xiguajerry.nullhack.utils.math.vectors.HAlign
import love.xiguajerry.nullhack.utils.math.vectors.Vec2d
import love.xiguajerry.nullhack.utils.math.vectors.Vec2i
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.glBlendFuncSeparate
import kotlin.math.min
import love.xiguajerry.nullhack.RenderSystem as RS

object NullHudEditor : NullHackGui<NullHudEditor>("${NullHackMod.NAME} HUDEditor") {
    override val self: NullHudEditor
        get() = this
    override val anime: MipmapTexture
        get() = ClickGui.animeType.texture
    override val animeHeight: Float
        get() = ClickGui.animeHeight
    override val animeXOffset: Float
        get() = ClickGui.animeXOffset
    override val bottomAlpha: Int
        get() = ClickGui.bottomAlpha
    override val clearBuffer: Boolean
        get() = ClickGui.clearBuffer
    override val mouseScrollSpeed: Int
        get() = HudEditor.mouseScrollSpeed
    override val pauseGame: Boolean
        get() = HudEditor.pauseGame
    override val categories: List<Category>
        get() = listOf(Category.HUD)
    override val outline: Boolean
        get() = HudEditor.outline
    override val settingModule: AbstractModule
        get() = HudEditor

    private val hudModules by CachedValueN(100) {
        panels.flatMap { it.children }.map { it.module }.filterIsInstance<HudModule>()
    }

    init {
        nonNullHandler<Render2DEvent> { e ->
            if (mc.screen != self) {
                hudModules.forEach {
                    if (it.isEnabled) it.onRender2D(it._x, it._y)
                }
            }
        }
    }

    override fun render(mouseX: Int, mouseY: Int) {
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
                val y = RS.scaledWidthF - picHeight
                anime.drawTexture(
                    x, y,
                    x + picWidth,
                    y + picHeight
                )

                translatef(0f, trans, 0f)

                panels.forEach {
                    it.render(mouseX, mouseY)
                }
            }

            val text = TextComponent()
            val textAlpha = if (mc.screen == self) 255 else min(alpha, 255)
            val watermark = "${NullHackMod.NAME} ${NullHackMod.TYPE}, "
            val copyright = "All rights reserved"
            text.addLine(watermark + copyright)
            text.draw(
                Vec2d(RS.scaledWidth, RS.scaledHeight - text.getHeight(0)),
                alpha = textAlpha.toFloat(), horizontalAlign = HAlign.RIGHT
            )

            if (mc.screen == self) {
                hudModules.forEach {
                    if (it.isEnabled) it.onRender2D(it._x, it._y)
                }
            }
        } catch (e: Exception) {
            NullHackMod.LOGGER.error("Exception was throw when rendering Gui:", e)
            e.printStackTrace()
        }
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (!super.mouseReleased(mouseX, mouseY, button)) {
            hudModules.forEach { if (it.onMouseRelease(Vec2i(mouseX, mouseY))) return true }
            false
        } else true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (!super.mouseClicked(mouseX, mouseY, button)){
            hudModules.forEach { if (it.onMouseClicked(Vec2i(mouseX, mouseY))) return true }
            false
        } else true
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        if (ImGui.getIO().wantCaptureMouse) return
        if (!handleMouseInput(mouseX.toInt(), mouseY.toInt()) && !handleMouseInputHud(mouseX.toInt(), mouseY.toInt())) {
            ImGui.setMouseCursor(CursorStyle.DEFAULT.imgui)
            GLFW.glfwSetCursor(mc.window.window, CursorStyle.DEFAULT.cursor)
        }
    }

    private fun handleMouseInputHud(mouseX: Int, mouseY: Int): Boolean {
        hudModules.forEach { if (it.onMouseMove(Vec2i(mouseX, mouseY))) return true }
        return false
    }
}