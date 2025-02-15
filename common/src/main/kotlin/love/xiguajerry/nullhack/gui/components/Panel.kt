package love.xiguajerry.nullhack.gui.components

import love.xiguajerry.nullhack.gui.Component
import love.xiguajerry.nullhack.gui.NullClickGui
import love.xiguajerry.nullhack.gui.NullHackGui
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.manager.managers.ModuleManager
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.impl.client.ClickGui
import love.xiguajerry.nullhack.modules.impl.client.Colors
import love.xiguajerry.nullhack.utils.MinecraftWrapper.mc
import love.xiguajerry.nullhack.graphics.GLHelper
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects.draw
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.matrix.MatrixLayerStack
import love.xiguajerry.nullhack.graphics.matrix.scope
import love.xiguajerry.nullhack.graphics.matrix.translatef
import love.xiguajerry.nullhack.graphics.shader.BlurRenderer
import love.xiguajerry.nullhack.utils.math.vectors.Vec2f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue
import love.xiguajerry.nullhack.RenderSystem as RS

class Panel<G : NullHackGui<G>>(
    x: Float, y: Float, private val gui: G,
    override var width0: Float, val category: Category
) : Component<G, ModuleComponent<G>>(x, y, gui) {
    override val width: Float
        get() = /*animationWidth.getAndUpdate(width0)*/ width0
    override val height: Float
        get() {
            val animheight = animationHeight.getAndUpdate(height0)
            return if (animheight > height0 && extended) height0
            else animheight
//            if (extended) height0 else animationHeight.getAndUpdate(height0)
        }
    override var height0: Float = 0f
        get() = if (extended) children.map { it.height }.sum() + LABEL_HEIGHT else LABEL_HEIGHT
    override val children: MutableList<ModuleComponent<G>> = mutableListOf()
    private var dragging = false
    private var offset = Vec2f.ZERO

    init {
        reset()
        resetPanel()
    }

    fun resetPanel() {
        children.clear()
        val modules = ModuleManager.getModulesByCategory(category)
            .filter { !it.internal && !it.hidden }.sortedBy { it.localizedName }
        modules.forEachIndexed { index, module ->
//            if (category == Category.CLIENT || category == Category.HUD)
            children.add(ModuleComponent(x, y + (index + 1) * (BASE_HEIGHT), width0, module, this)
                .also { it.extended = false })
        }
        extended = true
        animationHeight.forceUpdate(height0, height0)
    }

    override fun render(mouseX: Int, mouseY: Int) {
        GLHelper.blend = true
        RS.matrixLayer.scope {
            translatef(x, y, 0f)
            if (Colors.blur) BlurRenderer.render(0f, 0f, width, height)
            val bgColor = ColorRGBA(GuiManager.background)
            val tabColor = GuiManager.getColor()
            val tabX = if (ClickGui.outline) -OUTLINE_WIDTH else 0f
            val tabEndX = width + if (ClickGui.outline) OUTLINE_WIDTH else 0f
            GL_TRIANGLES.draw(PMVBObjects.VertexMode.Universal) {
                rectSeparate(width, height, bgColor)
                rectSeparate(tabX, 0f, tabEndX, LABEL_HEIGHT, tabColor)
            }
            if (ClickGui.outline) {
                Render2DUtils.drawRectOutline(-1f, 1f, width + 1, if (extended) height + 1 else height - 1,
                    OUTLINE_WIDTH, GuiManager.getColor())
            }
            val titleX = (width - UnicodeFontManager.CURRENT_FONT.getWidth(category.displayName.toString())) / 2f
            val titleY = (LABEL_HEIGHT - UnicodeFontManager.CURRENT_FONT.height) / 2
            UnicodeFontManager.CURRENT_FONT
                .drawStringWithShadow(category.localizedName, titleX, titleY, ColorRGBA.WHITE)
        }

        if ((this.height - LABEL_HEIGHT).absoluteValue <= 0.1f) return
        applyScissor {
            val w1 = mc.window.guiScaledWidth
            val h1 = mc.window.guiScaledHeight
            val mid1 = Vec2f(w1, h1) * 0.5f
            var currentY = this.y + LABEL_HEIGHT
            children.forEach {
                it.y = currentY
                val height = it.height
                currentY += height
//                if (!it.extended) {
//                    it.render(mouseX, mouseY)
//                    return@forEach
//                }
                if (it.y + height < 0f) return@forEach
                val w2 = it.width
                val h2 = height
                val mid2 = Vec2f(it.x + w2 / 2, it.y + h2 / 2)
                if (abs(mid2.x - mid1.x) < (w1 + w2) / 2 && abs(mid2.y - mid1.y) < (h1 + h2) / 2)
                    it.render(mouseX, mouseY)
            }
        }
    }

    context(MatrixLayerStack.MatrixScope)
    override fun drawTooltip(mouseX: Int, mouseY: Int): Boolean {
        children.forEach {
            if (it.drawTooltip(mouseX, mouseY)) return true

            it.children.forEach { s ->
                if (s.drawTooltip(mouseX, mouseY)) return true
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            dragging = false
        }
        children.forEach { it.mouseReleased(mouseX, mouseY, button) }
    }

    override fun clicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        return if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_2 && isHovered(mouseX, mouseY)) {
            extended = !extended
            true
        } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_1 && isHovered(mouseX, mouseY)) {
            dragging = true
            offset = Vec2f(mouseX - x, mouseY - y)
            if (mc.screen == NullClickGui)
                Collections.swap(gui.panels, 0, gui.panels.indexOf(this))
            true
        } else false
    }

    override fun onKeyTyped(typedChar: Char, keyCode: Int) {
        children.forEach { it.onKeyTyped(typedChar, keyCode) }
    }

    override fun isHovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX.toFloat() in x..x + width && mouseY.toFloat() in y..y + LABEL_HEIGHT
    }

    override fun move(x: Float, y: Float) {
        val offsetX = x - this.x
        val offsetY = y - this.y
        this.x += offsetX
        this.y += offsetY
        children.forEach { it.move(it.x + offsetX, it.y + offsetY) }
    }

    override fun handleMouseInput(mouseX: Int, mouseY: Int): Boolean {
        if (dragging) {
            move(mouseX - offset.x, mouseY - offset.y)
            return true
        }
        return super.handleMouseInput(mouseX, mouseY)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Panel<*>

        return category == other.category
    }

    override fun hashCode(): Int {
        return category.hashCode()
    }

    companion object {
        const val LABEL_HEIGHT = BASE_HEIGHT
    }
}