package love.xiguajerry.nullhack.gui.components

import com.mojang.blaze3d.platform.GlStateManager
import love.xiguajerry.nullhack.gui.NullHackGui
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.config.settings.AbstractSetting
import love.xiguajerry.nullhack.graphics.animations.AnimationFlag
import love.xiguajerry.nullhack.graphics.animations.Easing
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects.draw
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.matrix.scope
import love.xiguajerry.nullhack.graphics.matrix.translatef
import love.xiguajerry.nullhack.utils.MinecraftWrapper.mc
import love.xiguajerry.nullhack.utils.input.CursorStyle
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwGetKey
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import java.util.Stack
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import love.xiguajerry.nullhack.RenderSystem as RS

class StringSettingComponent<G : NullHackGui<G>>(
    x: Float, y: Float, width0: Float,
    setting: AbstractSetting<String, *>, father: ModuleComponent<G>
) : CommonSettingComponent<String, G>(x, y, width0, setting, father) {
    private var inputting = false
    private val animationX = AnimationFlag(Easing.OUT_CUBIC, DEFAULT_WIDTH_ANIM * 0.8f)
    private val currentContent0 = HistoriedCache(setting.value)
    private var currentContent by currentContent0
    private var currentCursorPos0 = HistoriedCache(currentContent.length)
    private var currentCursorPos by currentCursorPos0
    private val selectionRange = SelectionRange(currentCursorPos, currentCursorPos, Direction.RIGHT)
    override val cursorStyle: CursorStyle
        get() = CursorStyle.TYPE

    private fun backspace() {
        if (currentContent.isNotEmpty()) {
            if (selectionRange.isEmpty()) {
                if (currentCursorPos == 0) return

                currentContent = if (currentCursorPos == currentContent.length) {
                    currentContent.substring(0, currentContent.length - 1)
                } else {
                    currentContent.substring(0, currentCursorPos - 1) + currentContent.substring(currentCursorPos)
                }
                currentCursorPos--
            } else {
                currentContent = if (selectionRange.endInclusive == currentContent.length - 1)
                    currentContent.substring(0, selectionRange.start)
                else
                    currentContent.substring(0, selectionRange.start) + currentContent.substring(selectionRange.endInclusive + 1)
            }
        }
    }

    private fun delete() {
        if (currentContent.isNotEmpty()) {
            if (selectionRange.isEmpty()) {
                if (currentCursorPos != currentContent.length) {
                    currentContent = currentContent.substring(0, currentCursorPos) + currentContent.substring(currentCursorPos + 1)
                }
            } else {
                currentContent = if (selectionRange.endInclusive == currentContent.length - 1)
                    currentContent.substring(0, selectionRange.start)
                else
                    currentContent.substring(0, selectionRange.start) + currentContent.substring(selectionRange.endInclusive + 1)
            }
        }
    }

    override fun render(mouseX: Int, mouseY: Int) {
        if (inputting) UnicodeFontManager.CURRENT_FONT.cache(false)
        else currentContent = setting.value
        // if parent is the last module in the panel, then we should check how much the panel wraps the module component
        if (father.index == father.father.children.size - 1) {
            if ((father.father.y + father.father.height - (father.y + BASE_HEIGHT)).absoluteValue <= 0.1f) return
        } else if (father.index != father.father.children.size - 1) // otherwise, check the height of module directly
            if ((father.height - BASE_HEIGHT).absoluteValue <= 0.1f && !father.extended) return
        if (!setting.isVisible) {
            animationWidth.getAndUpdate(0f)
            return
        }

        val alpha = father.father.children.getOrNull(father.index + 1)?.let { nextModule ->
            if (y + height > nextModule.y) (1 - ((y + height - nextModule.y) / height).coerceIn(0f, 1f)) * 255f
            else 255f
        } ?: if (y + height <= father.father.y + father.father.height) 255f
        else max(0f, 255f - (((y + height) - (father.father.y + father.father.height))) / height * 255f)
        if (alpha.absoluteValue <= 5f) return
        if (isHovered(mouseX, mouseY)) {
            RS.matrixLayer.scope {
                drawInfo()
            }
        }
        RS.matrixLayer.scope {
            translatef(x, y, 0f)
            drawValue(mouseX, mouseY, alpha.toInt())
            val titleY = (BASE_HEIGHT - UnicodeFontManager.CURRENT_FONT.height) / 2
            if (!inputting) UnicodeFontManager.CURRENT_FONT.drawText(
                setting.localizedName, MINOR_X_PADDING, titleY, ColorRGBA.WHITE.alpha(alpha.toInt()))
        }
        UnicodeFontManager.CURRENT_FONT.cache(true)
    }

    override fun drawValue(mouseX: Int, mouseY: Int, fatherAlpha: Int) {
        if (inputting) {
            GlStateManager._disableScissorTest()
            val font = UnicodeFontManager.CURRENT_FONT
            val titleY = (BASE_HEIGHT - font.height) / 2
            val cursorX = animationX.getAndUpdate(
                MINOR_X_PADDING + font.getWidth(currentContent.substring(0, currentCursorPos)) + 0.0f)
            GL_TRIANGLES.draw(PMVBObjects.VertexMode.Universal) {
                rectSeparate(
                    MINOR_X_PADDING - 0.5f,
                    titleY - 1f,
                    MINOR_X_PADDING + font.getWidth(currentContent) + 0.5f,
                    titleY + font.height + 1f,
                    ColorRGBA(GuiManager.background)
                )
                if (!selectionRange.isEmpty()) {
                    rectSeparate(
                        MINOR_X_PADDING - 1f + font.getWidth(currentContent.substring(0, selectionRange.start)),
                        titleY - 1f,
                        MINOR_X_PADDING + font.getWidth(currentContent.substring(0, selectionRange.endInclusive + 1)) + 1f,
                        titleY + font.height + 1f,
                        ColorRGBA(GuiManager.color).alpha(GuiManager.background.alpha)
                    )
                }
                rectSeparate(
                    cursorX, VERTICAL_MARGIN,
                    cursorX + 1f, BASE_HEIGHT - VERTICAL_MARGIN,
                    GuiManager.getColor().alpha(fatherAlpha)
                )
            }
            font.drawText(currentContent, MINOR_X_PADDING, titleY, ColorRGBA.WHITE.alpha(fatherAlpha))
            GlStateManager._enableScissorTest()
        } else super.drawValue(mouseX, mouseY, fatherAlpha)
    }

    private fun findLastWordIndexBackward(): Int {
        if (currentCursorPos == 0) return 0
        val isLeftBlank = currentContent[currentCursorPos - 1].toString().isBlank()
        var result = currentCursorPos - 1
        while (result - 1 >= 0 && (
                    (isLeftBlank && currentContent[result - 1].toString().isBlank()) ||
                            (!isLeftBlank && !currentContent[result - 1].toString().isBlank())
                )
        ) {
            result--
        }
        return result
    }

    private fun findFirstWordIndexForward(): Int {
        if (currentCursorPos == currentContent.length) return currentCursorPos
        val isRightBlank = currentContent[currentCursorPos].toString().isBlank()
        var result = currentCursorPos
        while (result < currentContent.length && (
                    (isRightBlank && currentContent[result].toString().isBlank()) ||
                            (!isRightBlank && !currentContent[result].toString().isBlank())
                    )
        ) {
            result++
        }
        return result
    }

    override fun onKeyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) inputting = false
        else if (inputting) {
            if (typedChar != '\u0000') {
                if (currentCursorPos == currentContent.length) currentContent += typedChar
                else currentContent = currentContent.substring(0, currentCursorPos) +
                        typedChar + currentContent.substring(currentCursorPos)
                currentCursorPos++
            }
            if (glfwGetKey(GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS) {
                if (glfwGetKey(GLFW.GLFW_KEY_V) == GLFW.GLFW_PRESS) {
                    val clipboardContent = GLFW.glfwGetClipboardString(mc.window.window) ?: return
                    if (currentCursorPos == currentContent.length) currentContent += clipboardContent
                    else currentContent = currentContent.substring(0, currentCursorPos) +
                            clipboardContent + currentContent.substring(currentCursorPos)
                    currentCursorPos += clipboardContent.length
                } else if (glfwGetKey(GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS) {
                    val stringToCopy = selectionRange.selectedString ?: currentContent
                    GLFW.glfwSetClipboardString(mc.window.window, stringToCopy)
                } else if (glfwGetKey(GLFW.GLFW_KEY_X) == GLFW.GLFW_PRESS) {
                    if (!selectionRange.isEmpty()) {
                        val stringToCopy = selectionRange.selectedString ?: currentContent
                        GLFW.glfwSetClipboardString(mc.window.window, stringToCopy)
                        delete()
                    }
                } else if (glfwGetKey(GLFW.GLFW_KEY_Z) == GLFW.GLFW_PRESS) { // recall
                    if (glfwGetKey(GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
                        currentContent0.redo() // ensure compatibility with IDEA keybindings
                        currentCursorPos0.redo()
                    } else {
                        currentContent0.undo()
                        currentCursorPos0.undo()
                    }
                } else if (glfwGetKey(GLFW.GLFW_KEY_Y) == GLFW.GLFW_PRESS) {
                    currentContent0.redo()
                    currentCursorPos0.redo()
                } else if (glfwGetKey(GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
                    selectionRange.set(0, currentContent.length)
                    currentCursorPos = currentContent.length
                } else if (glfwGetKey(GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
                    val lastCursorPos = currentCursorPos
                    currentCursorPos = findLastWordIndexBackward()
                    if (glfwGetKey(GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
                        if (selectionRange.isEmpty()) {
                            selectionRange.set(currentCursorPos, lastCursorPos)
                            selectionRange.direction = Direction.LEFT
                        } else {
                            selectionRange.apply {
                                if (direction == Direction.LEFT) {
                                    set(currentCursorPos, endInclusive + 1)
                                } else {
                                    if (currentCursorPos < start) {
                                        direction = Direction.LEFT
                                        set(currentCursorPos, start + 1)
                                    } else {
                                        set(start, currentCursorPos)
                                    }
                                }
                            }
                        }
                    } else {
                        if (!selectionRange.isEmpty()) selectionRange.clear()
                    }
                } else if (glfwGetKey(GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
                    val lastCursorPos = currentCursorPos
                    currentCursorPos = findFirstWordIndexForward()
                    if (glfwGetKey(GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
                        if (selectionRange.isEmpty()) {
                            selectionRange.set(lastCursorPos, currentCursorPos)
                            selectionRange.direction = Direction.RIGHT
                        } else {
                            selectionRange.apply {
                                if (direction == Direction.RIGHT) {
                                    set(start, currentCursorPos)
                                } else {
                                    if (currentCursorPos > endInclusive + 1) {
                                        direction = Direction.RIGHT
                                        set(endInclusive, currentCursorPos)
                                    } else {
                                        set(currentCursorPos, endInclusive + 1)
                                    }
                                }
                            }
                        }
                    } else {
                        if (!selectionRange.isEmpty()) selectionRange.clear()
                    }
                }
            } else {
                when (keyCode) {
                    GLFW.GLFW_KEY_BACKSPACE -> {
                        backspace()
                    }
                    GLFW.GLFW_KEY_DELETE -> {
                        delete()
                    }
                    GLFW.GLFW_KEY_ENTER -> {
                        inputting = false
                        setting.value = currentContent
                    }
                    GLFW.GLFW_KEY_LEFT -> {
                        if (glfwGetKey(GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
                            if (selectionRange.isEmpty()) {
                                selectionRange.apply {
                                    clear()
                                    direction = Direction.LEFT
                                }
                            }
                            selectionRange.apply {
                                if (direction == Direction.LEFT) expand()
                                else shrink()
                            }
                            currentCursorPos = max(0, currentCursorPos - 1)
                        } else {
                            if (selectionRange.isEmpty())
                                currentCursorPos = max(0, currentCursorPos - 1)
                            else selectionRange.clear()
                        }
                    }
                    GLFW.GLFW_KEY_RIGHT -> {
                        if (glfwGetKey(GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
                            if (selectionRange.isEmpty()) {
                                selectionRange.apply {
                                    clear()
                                    direction = Direction.RIGHT
                                }
                            }
                            selectionRange.apply {
                                if (direction == Direction.RIGHT) expand()
                                else shrink()
                            }
                            currentCursorPos = min(currentContent.length, currentCursorPos + 1)
                        } else {
                            if (selectionRange.isEmpty())
                                currentCursorPos = min(currentContent.length, currentCursorPos + 1)
                            else selectionRange.clear()
                        }
                    }
                }
            }
        }
    }

    private fun glfwGetKey(keyCode: Int) = glfwGetKey(mc.window.window, keyCode)

    override fun clicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (mouseButton == 0) {
            animationX.forceUpdate(0f, 0f)
            if (inputting) {
                setting.value = currentContent
                currentContent0.setAndClear(currentContent)
                currentCursorPos = currentContent.length
                currentCursorPos0.setAndClear(currentCursorPos)
                inputting = !inputting
                return true
            } else if (isHovered(mouseX, mouseY)) {
                currentContent0.setAndClear(setting.value)
                currentCursorPos0.setAndClear(currentContent.length)
                inputting = !inputting
                return true
            }
        }
        return super.clicked(mouseX, mouseY, mouseButton)
    }

    private inner class SelectionRange(
        private var currentStart: Int,
        private var currentEnd: Int,
        var direction: Direction
    ) : ClosedRange<Int> {
        override val start: Int
            get() = currentStart
        override val endInclusive: Int
            get() = currentEnd - 1
        val selectedString: String?
            get() = if (isEmpty()) null else currentContent.substring(currentStart, currentEnd)

        override fun isEmpty(): Boolean {
            return currentStart == currentEnd
        }

        fun expand() {
            if (direction == Direction.LEFT) currentStart = max(0, currentStart - 1)
            else currentEnd = min(currentContent.length, currentEnd + 1)
        }

        fun shrink() {
            if (direction == Direction.LEFT) currentStart = min(currentContent.length, currentStart + 1)
            else currentEnd = max(0, currentEnd - 1)

        }

        fun set(start: Int, end: Int) {
            require(start <= end)
            currentStart = start
            currentEnd = end
        }

        fun clear() {
            currentStart = currentCursorPos
            currentEnd = currentStart
        }
    }

    enum class Direction {
        LEFT, RIGHT
    }

    private class HistoriedCache<T>(private var value: T) : ReadWriteProperty<Any?, T> {
        private val history = Stack<T>()
        private val redoStack = Stack<T>()

        /**
         * Set the value to the latest history
         */
        fun undo() {
            if (history.isNotEmpty()) {
                redoStack.push(value)
                value = history.pop()
            }
        }

        fun redo() {
            if (redoStack.isNotEmpty()) {
                history.push(value)
                value = redoStack.pop()
            }
        }

        fun get() = value

        fun set(newValue: T) {
            redoStack.clear() // abandon reverted changes
            history.push(value)
            value = newValue
        }

        fun setAndClear(newValue: T) {
            history.clear()
            redoStack.clear()
            value = newValue
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return get()
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            set(value)
        }
    }
}