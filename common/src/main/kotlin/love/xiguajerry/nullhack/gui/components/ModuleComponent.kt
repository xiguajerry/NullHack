package love.xiguajerry.nullhack.gui.components

import imgui.ImGui
import love.xiguajerry.nullhack.RenderSystem
import love.xiguajerry.nullhack.gui.Component
import love.xiguajerry.nullhack.gui.NullHackGui
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.modules.AbstractModule
import love.xiguajerry.nullhack.modules.impl.client.Colors
import love.xiguajerry.nullhack.config.settings.*
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings
import love.xiguajerry.nullhack.utils.MinecraftWrapper.mc
import love.xiguajerry.nullhack.graphics.animations.AnimationFlag
import love.xiguajerry.nullhack.graphics.animations.Easing
import love.xiguajerry.nullhack.graphics.buffer.Render2DUtils
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects.draw
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.font.IconMapper
import love.xiguajerry.nullhack.graphics.font.TextComponent
import love.xiguajerry.nullhack.graphics.matrix.MatrixLayerStack
import love.xiguajerry.nullhack.graphics.matrix.scope
import love.xiguajerry.nullhack.graphics.matrix.translatef
import love.xiguajerry.nullhack.graphics.shader.BlurRenderer
import love.xiguajerry.nullhack.utils.input.CursorStyle
import love.xiguajerry.nullhack.utils.math.vectors.HAlign
import love.xiguajerry.nullhack.utils.math.vectors.VAlign
import love.xiguajerry.nullhack.utils.math.vectors.Vec2f
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import kotlin.math.abs
import kotlin.math.max
import love.xiguajerry.nullhack.RenderSystem as RS

@Suppress("UNCHECKED_CAST")
class ModuleComponent<G : NullHackGui<G>>(
    x: Float, y: Float,
    override var width0: Float, val module: AbstractModule,
    override val father: Panel<G>
) : Component<Panel<G>, CommonSettingComponent<*, G>>(x, y, father) {
    override val width: Float
        get() = /*animationWidth.getAndUpdate(width0)*/ width0
    override val height: Float
        get() = animationHeight.getAndUpdate(height0)
    override var height0 = BASE_HEIGHT
        get() =
            if (extended) children.filter { it.setting.isVisible }.map { it.height }.sum() + BASE_HEIGHT else BASE_HEIGHT
    override val children: MutableList<CommonSettingComponent<*, G>> = mutableListOf()
    private val animX = AnimationFlag(Easing.OUT_CUBIC, DEFAULT_WIDTH_ANIM * 1.2f)
    override val cursorStyle: CursorStyle
        get() = CursorStyle.CLICK

    init {
        reset()
        resetModule()
    }

    val index get() = father.children.indexOf(this)
//    val yStep get() = father.y + Panel.LABEL_HEIGHT + father.children.subList(0, index).map { it.height }.sum()

    private fun registerSetting(list: MutableList<CommonSettingComponent<*, G>>, setting: AbstractSetting<*, *>) {
        when (setting) {
            is BooleanSetting ->
                list.add(BooleanSettingComponent(x, y + (index + 1) * (BASE_HEIGHT), width0, setting, this))
            is DoubleSetting ->
                list.add(DoubleSettingComponent(x, y + (index + 1) * (BASE_HEIGHT), width0, setting, this))
            is StringSetting  ->
                list.add(StringSettingComponent(x, y + (index + 1) * (BASE_HEIGHT), width0, setting, this))
            is FloatSetting   ->
                list.add(FloatSettingComponent(x, y + (index + 1) * (BASE_HEIGHT), width0, setting, this))
            is ColorSetting   ->
                list.add(ColorSettingComponent(x, y + (index + 1) * (BASE_HEIGHT), width0, setting, this))
            is EnumSetting<*> ->
                list.add(EnumSettingComponent(x, y + (index + 1) * (BASE_HEIGHT), width0,
                    setting as AbstractSetting<Enum<*>, *>, this))
            is BindSetting ->
                list.add(BindSettingComponent(x, y + (index + 1) * (BASE_HEIGHT), width0, setting, this))
            is IntSetting ->
                list.add(IntSettingComponent(x, y + (index + 1) * (BASE_HEIGHT), width0, setting, this))
            is LabelSetting ->
                list.add(LabelSettingComponent(x, y + (index + 1) * (BASE_HEIGHT), width0, setting, this))
            else              -> {}
//                list.add(CommonSettingComponent(x, y + (index + 1) * (BASE_HEIGHT), width0, setting, this))
        }
    }

    private fun resetModule() {
        children.clear()
        val settings = module.filteredSettings
        settings.forEach { setting ->
            registerSetting(children, setting)
        }
        animationHeight.forceUpdate(height0, height0)
        extended = false
    }

    override fun render(mouseX: Int, mouseY: Int) {
        RS.matrixLayer.scope {
            val rectEndX = animX.getAndUpdate(width) - HORIZONTAL_MARGIN
            val rectColor = GuiManager.getColor(index * 100)
            translatef(x, y, 0f)
            GL_TRIANGLES.draw(PMVBObjects.VertexMode.Universal) {
                if (module.isEnabled) {
                    rectSeparate(
                        HORIZONTAL_MARGIN, VERTICAL_MARGIN,
                        rectEndX,
                        BASE_HEIGHT - VERTICAL_MARGIN,
                        rectColor.alpha(Colors.enabledAlpha)
                    )
                } else {
                    rectSeparate(
                        HORIZONTAL_MARGIN, VERTICAL_MARGIN,
                        width - HORIZONTAL_MARGIN, BASE_HEIGHT - VERTICAL_MARGIN,
                        rectColor.alpha(Colors.unfocusedAlpha)
                    )
                    if (!ImGui.getIO().wantCaptureMouse) {
                        if (!isHovered(mouseX, mouseY) && !module.isEnabled) animX.getAndUpdate(0f)
                        if (isHovered(mouseX, mouseY)) {
                            rectSeparate(
                                HORIZONTAL_MARGIN, VERTICAL_MARGIN, rectEndX,
                                BASE_HEIGHT - VERTICAL_MARGIN, rectColor
                                    .alpha(Colors.hoveredAlpha - Colors.unfocusedAlpha)
                            )
                        }
                    }
                }
            }

            val titleY = (BASE_HEIGHT - UnicodeFontManager.CURRENT_FONT.height) / 2
            val icon = IconMapper.PAPER_CLIP
            val iconX = width0 - MAJOR_X_PADDING - UnicodeFontManager.ICON_FONT.getWidth(icon)
            val iconY = (BASE_HEIGHT - UnicodeFontManager.ICON_FONT.height) / 2
            val moduleName =
                /*if (UnicodeFontManager.CURRENT_FONT.getWidth(module.localizedName) > iconX - MAJOR_X_PADDING)
                    buildString {
                        var p = 0
                        val currentWidth = UnicodeFontManager.CURRENT_FONT.getWidth("${toString()}...")
                        while (currentWidth < iconX - MAJOR_X_PADDING) {
                            val c = module.localizedName[p]
                            if (currentWidth > iconX - MAJOR_X_PADDING) {
                                append("..."); break
                            } else {
                                append(c); p++
                            }
                        }
                        append("...")
                    }
                else*/ module.localizedName
            val alpha = if (father.height + father.y > this@ModuleComponent.y + BASE_HEIGHT) 255
            else max(0f, 255f - (((BASE_HEIGHT + this@ModuleComponent.y) - (father.height + father.y)) / BASE_HEIGHT) * 255f).toInt()
            if (alpha > 10) {
                UnicodeFontManager.CURRENT_FONT.drawText(moduleName, MAJOR_X_PADDING, titleY, ColorRGBA.WHITE.alpha(alpha))
                UnicodeFontManager.ICON_FONT.drawText(icon, iconX, iconY, ColorRGBA.WHITE.alpha(alpha))
            }
        }
        if (abs(this.height - BASE_HEIGHT) < 0.01 && index != father.children.size - 1) return
        val w1 = mc.window.guiScaledWidth
        val h1 = mc.window.guiScaledHeight
        val mid1 = Vec2f(w1, h1) * 0.5f
//        applyScissor {
            var currentY = this.y + BASE_HEIGHT
            children.forEach {
                it.y = currentY
                val w2 = it.width
                val h2 = it.height
                if (it.setting.isVisible) currentY += h2
                val mid2 = Vec2f(it.x + w2 / 2, it.y + h2 / 2)
                if (abs(mid2.x - mid1.x) < (w1 + w2) / 2 && abs(mid2.y - mid1.y) < (h1 + h2) / 2)
                    it.render(mouseX, mouseY)
            }
//        }
    }

    context(MatrixLayerStack.MatrixScope)
    override fun drawTooltip(mouseX: Int, mouseY: Int): Boolean {
        if (father.extended && isHovered(mouseX, mouseY)) {
            drawInfo()
            return true
        }
        return false
    }

    context(MatrixLayerStack.MatrixScope)
    private fun drawInfo() {
        if (mc.screen != this.father.father) return

        val mouseX = mc.mouseHandler.xpos() * RenderSystem.scaledWidth / RenderSystem.widthD
        val mouseY = mc.mouseHandler.ypos() * RenderSystem.scaledHeight / RenderSystem.heightD

        val info = TextComponent()
        info.addLine("Name: ${module.localizedName}")
        info.addLine("Enabled: ${module.isEnabled}")
        if (module.description != "") info.addLine("Description: ${module.description}")

        var startX = mouseX - 4.0
        val startY = mouseY
        val vAlign = if (startY + info.getHeight(1) > RenderSystem.scaledHeight) {
            VAlign.BOTTOM
        } else VAlign.TOP
        val hAlign = if (startX + info.getWidth() > RenderSystem.scaledWidth) HAlign.RIGHT else {
            startX += 10.0
            HAlign.LEFT
        }

        translatef(startX.toFloat(), startY.toFloat(), 100f)
        val rectWidth = info.getWidth()
        val rectHeight = info.getHeight(1)
        val rectX = if (hAlign == HAlign.LEFT) 0f else -rectWidth
        val rectY = if (vAlign == VAlign.TOP) 0f else -rectHeight
        if (Colors.blur) BlurRenderer.render(
            rectX,
            rectY,
            rectX + rectWidth,
            rectY + rectHeight,
            ClientSettings.windowBlurPass
        )
        Render2DUtils.drawRect(
            rectX,
            rectY,
            rectX + rectWidth,
            rectY + rectHeight,
            ColorRGBA(GuiManager.background)
        )
        info.draw(lineSpace = 1, verticalAlign = vAlign, horizontalAlign = hAlign)
    }

    override fun move(x: Float, y: Float) {
        val offsetX = x - this.x
        val offsetY = y - this.y
        this.x += offsetX
        this.y += offsetY
        children.forEach { it.move(it.x + offsetX, it.y + offsetY) }
    }

    override fun clicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        return if (mouseButton == 1 && isHovered(mouseX, mouseY)) {
            extended = !extended
            true
        } else if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
            module.toggle()
            true
        } else false
    }

    override fun onKeyTyped(typedChar: Char, keyCode: Int) {
        children.forEach { it.onKeyTyped(typedChar, keyCode) }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        children.forEach { it.mouseReleased(mouseX, mouseY, button) }
    }
}