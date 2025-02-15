package love.xiguajerry.nullhack.gui

import love.xiguajerry.nullhack.config.settings.ColorSetting
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects.draw
import love.xiguajerry.nullhack.graphics.color.ColorHSVA
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.utils.math.vectors.Vec2f
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

class ColorPicker(private val initial: ColorSetting, private val svWidth: Float, val father: Screen? = null)
    : Screen(Component.literal("ColorPicker for ${initial.name}")) {
    var x = 50.0f
    var y = 50.0f
    var headless = false
    var xOff = 0f
    var yOff = 0f
    val actualWidth = 1 + svWidth + GAP + RGB_WIDTH + GAP + ALPHA_WIDTH + 1
    val actualHeight = 1 + svWidth + 1
    private val svX get() = x + 1
    private val svY get() = y + 1
    private val hX get() = svX + svWidth + GAP
    private val hY get() = svY
    private val aX get() = hX + RGB_WIDTH + GAP
    private val aY get() = hY
    private var pointSV = Vec2f()
    private var pointH = Vec2f()
    private var pointA = Vec2f()
    private var pure = initial.value.toHSB().brightness(1f).saturation(1f).alpha(1f)
    private var lockType = LockType.NONE
    val isSlidingWithTop get() = lockType == LockType.TOP
    val isSliding get() = lockType != LockType.NONE && lockType != LockType.TOP

    companion object {
        const val GAP = 4f
        const val RGB_WIDTH = 5f
        const val ALPHA_WIDTH = 5f
        const val TOP_HEIGHT = 15f
    }

    init {
        update()
    }

    fun update() {
        val color = initial.value.toHSB()
        val x = (color.s * svWidth).toInt()
        val y = ((1f - color.b) * svWidth).toInt()
        pointSV = Vec2f(x, y)
        val y2 = (color.h * svWidth).toInt()
        pointH = Vec2f(1, y2)
        val y3 = (color.a * svWidth).toInt()
        pointA = Vec2f(1, y3)
    }

    override fun isPauseScreen(): Boolean {
        return father?.isPauseScreen == true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            lockType = when {
                isHoveredWithSV(mouseX.toInt(), mouseY.toInt()) -> LockType.SV
                isHoveredWithH(mouseX.toInt(), mouseY.toInt()) -> LockType.H
                isHoveredWithA(mouseX.toInt(), mouseY.toInt()) -> LockType.A
                isHoveredWithTop(mouseX.toInt(), mouseY.toInt()) && !headless -> LockType.TOP
                else -> LockType.NONE
            }
            val clicked = lockType != LockType.NONE
            if (clicked) {
                if (lockType == LockType.TOP) {
                    xOff = x - mouseX.toInt()
                    yOff = y - mouseY.toInt()
                }
            }
            return clicked
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        lockType = LockType.NONE
        return true
    }

    private val hueList = listOf(
        ColorRGBA.RED,     // 0.0000
        ColorRGBA.YELLOW,  // 0.1666
        ColorRGBA.GREEN ,  // 0.3333
        ColorRGBA.CYAN,    // 0.5000
        ColorRGBA.BLUE,    // 0.6666
        ColorRGBA.MAGENTA  // 0.8333
    )

    override fun render(unused: GuiGraphics?, mouseX: Int, mouseY: Int, delta: Float) {
        if (isSlidingWithTop) {
            x = mouseX + xOff
            y = mouseY + yOff
        } else if (isSliding) {
            when (lockType) {
                LockType.SV -> {
                    val deltaX = mouseX - svX
                    val deltaY = mouseY - svY
                    pointSV = Vec2f(deltaX, deltaY)
                }
                LockType.H -> {
                    val deltaY = mouseY - hY
                    pointH = Vec2f(1f, deltaY)
                }
                LockType.A -> {
                    val deltaY = mouseY - aY
                    pointA = Vec2f(1f, deltaY)
                }
                else -> {}
            }
        }
        pointSV = Vec2f(pointSV.x.coerceIn(0f..svWidth), pointSV.y.coerceIn(0f..svWidth))
        pointH = Vec2f(pointH.x.coerceIn(0f..RGB_WIDTH), pointH.y.coerceIn(0f..svWidth))
        pointA = Vec2f(pointA.x.coerceIn(0f..ALPHA_WIDTH), pointA.y.coerceIn(0f..svWidth))
        pure = ColorHSVA(pointH.y / svWidth, 1f, 1f, 1f)
        val nonAlpha = pure.saturation(pointSV.x / svWidth).brightness((svWidth - pointSV.y) / svWidth).toRGBA()
        initial.value = nonAlpha.alpha(((pointA.y / svWidth) * 255).toInt())

        if (!headless) {
            Render2DUtils.drawRect(x, y - TOP_HEIGHT,  x + actualWidth, y, initial.value)
            UnicodeFontManager.CURRENT_FONT.drawStringWithShadow("Editing: ${initial.name}", x + 1f, y - TOP_HEIGHT + 1f)
            Render2DUtils.drawRect(x, y, x + actualWidth, y + actualHeight, ColorRGBA(GuiManager.background))
            Render2DUtils.drawRectOutline(x, y, x + actualWidth, y + actualHeight, 1f, ColorRGBA(GuiManager.color))
        }
        // Saturation & Brightness
        GL11.GL_TRIANGLE_STRIP.draw(PMVBObjects.VertexMode.Universal) {
            universal(svX + svWidth, svY, pure.toRGBA())
            universal(svX, svY, ColorRGBA.WHITE)
            universal(svX + svWidth, svY + svWidth, pure.toRGBA())
            universal(svX, svY + svWidth, ColorRGBA.WHITE)
        }
        val empty = ColorRGBA.BLACK.alpha(0)
        GL11.GL_TRIANGLE_STRIP.draw(PMVBObjects.VertexMode.Universal) {
            universal(svX + svWidth, svY, empty)
            universal(svX, svY, empty)
            universal(svX + svWidth, svY + svWidth, ColorRGBA.BLACK)
            universal(svX, svY + svWidth, ColorRGBA.BLACK)
        }
        // Hue
        val segLength = svWidth / 6f
        GL11.GL_TRIANGLE_STRIP.draw(PMVBObjects.VertexMode.Universal) {
            for((i, color) in hueList.withIndex()) {
                val hueY = hY + segLength * i
                universal(hX + RGB_WIDTH, hueY, color)
                universal(hX, hueY, color)
            }
            universal(hX + RGB_WIDTH, hY + svWidth, hueList[0])
            universal(hX, hY + svWidth, hueList[0])
        }
        // Alpha
        GL11.GL_TRIANGLE_STRIP.draw(PMVBObjects.VertexMode.Universal) {
            universal(aX, aY, nonAlpha.alpha(0))
            universal(aX, aY + svWidth, nonAlpha)
            universal(aX + ALPHA_WIDTH, aY, nonAlpha.alpha(0))
            universal(aX + ALPHA_WIDTH, aY + svWidth, nonAlpha)
        }

        val sv = pointSV + Vec2f(svX, svY)
        val h = pointH + Vec2f(hX, hY)
        val a = pointA + Vec2f(aX, aY)
        Render2DUtils.drawRectOutline(sv.x - 2f, sv.y - 2f, sv.x + 2f, sv.y + 2f, 2f, ColorRGBA.WHITE)
        Render2DUtils.drawRectOutline(h.x - 1f, h.y, h.x + RGB_WIDTH - 1f, h.y, 2f, ColorRGBA.WHITE)
        Render2DUtils.drawRectOutline(a.x - 1f, a.y, a.x + ALPHA_WIDTH - 1f, a.y, 2f, ColorRGBA.WHITE)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) Minecraft.getInstance().setScreen(father)
        return true
    }

    private fun isHoveredWithTop(mouseX: Int, mouseY: Int): Boolean {
        return mouseX.toFloat() in x..(x + actualWidth) && mouseY.toFloat() in(y - TOP_HEIGHT)..y
    }

    private fun isHoveredWithSV(mouseX: Int, mouseY: Int): Boolean {
        return mouseX.toFloat() in svX..(svX + svWidth) && mouseY.toFloat() in svY..(svY + svWidth)
    }

    private fun isHoveredWithH(mouseX: Int, mouseY: Int): Boolean {
        return mouseX.toFloat() in hX..(hX + RGB_WIDTH) && mouseY.toFloat() in hY..(hY + svWidth)
    }

    private fun isHoveredWithA(mouseX: Int, mouseY: Int): Boolean {
        return mouseX.toFloat() in aX..(aX + ALPHA_WIDTH) && mouseY.toFloat() in aY..(aY + svWidth)
    }

    private fun getCurrentColor(): ColorHSVA {
        return ColorHSVA(pointH.y / svWidth, pointSV.x / svWidth, 1f - (pointSV.y / svWidth))
    }

    private enum class LockType {
        NONE, SV, H, A, TOP
    }
}