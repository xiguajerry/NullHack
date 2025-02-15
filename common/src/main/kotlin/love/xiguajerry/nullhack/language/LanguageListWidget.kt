package love.xiguajerry.nullhack.language

import love.xiguajerry.nullhack.interfaces.ILanguageOptionsScreen
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ObjectSelectionList
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.options.LanguageSelectScreen
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import kotlin.math.min

class LanguageListWidget(
    val client: Minecraft,
    val screen: LanguageSelectScreen,
    width: Int,
    height: Int,
    private val title: Component
) : ObjectSelectionList<LanguageEntry>(client, width, height - 83 - 16, 32 + 16, 24, (9f * 1.5f).toInt()) {

    init {
        centerListVertically = false
    }

    override fun renderHeader(context: GuiGraphics, x: Int, y: Int) {
        val headerText = title.copy().withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD)
        val headerPosX = x + width / 2 - client.font.width(headerText) / 2
        val headerPosY = min(this.y + 3, y)
        context.drawString(client.font, headerText, headerPosX, headerPosY, 0xFFFFFF, false)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val selectedEntry = this.selected
        if (selectedEntry == null) return super.keyPressed(keyCode, scanCode, modifiers)

        if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER) {
            selectedEntry.toggle()
            this.setFocused(null)
            (screen as ILanguageOptionsScreen).languagereload_focusEntry(selectedEntry)
            return true
        }

        if (Screen.hasShiftDown()) {
            if (keyCode == GLFW.GLFW_KEY_DOWN) {
                selectedEntry.moveDown()
                return true
            }
            if (keyCode == GLFW.GLFW_KEY_UP) {
                selectedEntry.moveUp()
                return true
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun getEntryAtPosition(x: Double, y: Double): LanguageEntry? {
        var entry = super.getEntryAtPosition(x, y)
        return if (entry != null && this.scrollbarVisible() && x >= this.scrollBarX()) null else entry
    }

    protected override fun renderSelection(
        context: GuiGraphics,
        y: Int,
        entryWidth: Int,
        entryHeight: Int,
        borderColor: Int,
        fillColor: Int
    ) {
        if (this.scrollbarVisible()) {
            var x1 = this.rowLeft - 2
            var x2 = this.scrollBarX()
            var y1 = y - 2
            var y2 = y + entryHeight + 2
            context.fill(x1, y1, x2, y2, borderColor)
            context.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillColor)
        } else {
            super.renderSelection(context, y, entryWidth, entryHeight, borderColor, fillColor)
        }
    }

    fun getHoveredSelectionRight(): Int {
        return if (this.scrollbarVisible()) this.scrollBarX() else this.rowRight - 2
    }

    val rowHeight get() = itemHeight

    override fun getRowWidth(): Int {
        return width
    }

    override fun scrollBarX(): Int {
        return this.right - 6
    }
}
