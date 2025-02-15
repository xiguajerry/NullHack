package love.xiguajerry.nullhack.modules.impl.client

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.TickEvent
import love.xiguajerry.nullhack.event.impl.render.CoreRender2DEvent
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.graphics.font.UnicodeFontRenderer
import love.xiguajerry.nullhack.utils.runSafe

object RefreshFontCache : Module("Refresh FontCache", category = Category.CLIENT, alwaysListening = true) {
    private val showAtlas by setting("Show Atlas", false)
    private val atlasIndex by setting("Atlas Index", 0, { showAtlas })
    private val containChar by setting("Contain Char", "a")
    private val showStaticString by setting("Show Static String", false)

    init {
        nonNullHandler<TickEvent.Post> {
            disable()
        }

        nonNullHandler<CoreRender2DEvent> {
            if (showAtlas) {
                UnicodeFontManager.CURRENT_FONT.drawAtlas(atlasIndex)
            }
        }

        onEnabled {
            runSafe {
                UnicodeFontRenderer.refresh()
                disable()
            } ?: disable()
            disable()
        }
    }
}