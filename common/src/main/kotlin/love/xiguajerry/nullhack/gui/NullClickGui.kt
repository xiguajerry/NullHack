package love.xiguajerry.nullhack.gui

import love.xiguajerry.nullhack.modules.AbstractModule
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.impl.client.ClickGui
import love.xiguajerry.nullhack.graphics.texture.MipmapTexture

object NullClickGui : NullHackGui<NullClickGui>("NullHack ClickGui") {
    override val self: NullClickGui
        get() = this
    override val anime: MipmapTexture
        get() = ClickGui.animeType.texture
    override val mouseScrollSpeed: Int
        get() = ClickGui.mouseScrollSpeed
    override val pauseGame: Boolean
        get() = ClickGui.pauseGame
    override val categories: List<Category>
        get() = Category.entries.filter { it != Category.HUD }
    override val animeHeight: Float
        get() = ClickGui.animeHeight
    override val animeXOffset: Float
        get() = ClickGui.animeXOffset
    override val bottomAlpha: Int
        get() = ClickGui.bottomAlpha
    override val clearBuffer: Boolean
        get() = ClickGui.clearBuffer
    override val outline: Boolean
        get() = ClickGui.outline
    override val settingModule: AbstractModule
        get() = ClickGui

    override fun process() {
//        ImGui.showDemoWindow()
    }
}