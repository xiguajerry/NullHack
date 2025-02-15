package love.xiguajerry.nullhack.gui

import love.xiguajerry.nullhack.utils.MinecraftWrapper
import love.xiguajerry.nullhack.graphics.animations.AnimationFlag
import love.xiguajerry.nullhack.graphics.animations.Easing
import love.xiguajerry.nullhack.graphics.matrix.MatrixLayerStack
import love.xiguajerry.nullhack.utils.input.CursorStyle
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW
import love.xiguajerry.nullhack.RenderSystem as RS

abstract class Component<F : Any, C : Any>(
    @Volatile var x: Float, @Volatile var y: Float,
    open val father: F?
) {
    abstract var width0: Float
    abstract var height0: Float
    abstract val width: Float
    abstract val height: Float
    protected val animationWidth = AnimationFlag(Easing.OUT_CUBIC, DEFAULT_WIDTH_ANIM)
    val animationHeight = AnimationFlag(Easing.OUT_CUBIC, DEFAULT_HEIGHT_ANIM)
    abstract val children: MutableList<C>
    protected open val cursorStyle = CursorStyle.DEFAULT
    @Volatile
    var extended = false

    abstract fun render(mouseX: Int, mouseY: Int)

    context(MatrixLayerStack.MatrixScope)
    abstract fun drawTooltip(mouseX: Int, mouseY: Int): Boolean

    /**
     * @return `true` if event is cancelled
     */
    abstract fun clicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean

    open fun init() {
        children.forEach { if (it is Component<*, *>) it.init() }
    }

    open fun mouseReleased(mouseX: Int, mouseY: Int, button: Int = GLFW.GLFW_MOUSE_BUTTON_1) {}

    open fun onKeyTyped(typedChar: Char, keyCode: Int) {  }

    open fun handleMouseInput(mouseX: Int, mouseY: Int): Boolean {
        return if (isHovered(mouseX, mouseY)) {
            GLFW.glfwSetCursor(MinecraftWrapper.mc.window.window, cursorStyle.cursor)
            true
        } else false
    }

    fun reset() {
        animationWidth.forceUpdate(width, width)
        animationHeight.forceUpdate(height, height)
    }

    open fun move(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun moveToMouse(mouseX: Int, mouseY: Int) = move(mouseX.toFloat(), mouseY.toFloat())

    open fun isHovered(mouseX: Int, mouseY: Int) =
        mouseX.toFloat() in x..(x + width) && mouseY.toFloat() in y..(y + BASE_HEIGHT)

    protected fun applyScissor(func: () -> Unit) {
        val trans = RS.matrixLayer.peek.position
        val start = Vector4f(x, y, 0f, 1f).mul(trans)
        val end = Vector4f(x + width, y + height, 0f, 1f).mul(trans)
        RS.applyScissor(start.x.toInt(), start.y.toInt(), end.x.toInt(), end.y.toInt(), func)
    }

    companion object {
        const val DEFAULT_HEIGHT_ANIM = 400f
        const val DEFAULT_WIDTH_ANIM = 400f
        const val OUTLINE_WIDTH = 1.0f
        const val VERTICAL_MARGIN = 0.5f
        const val HORIZONTAL_MARGIN = VERTICAL_MARGIN * 2
        const val BASE_HEIGHT = 14f + VERTICAL_MARGIN * 2
        const val MAJOR_X_PADDING = 2f
        const val MINOR_X_PADDING = 3.5f
    }
}