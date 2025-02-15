package love.xiguajerry.nullhack.gui.hud.impl

import love.xiguajerry.nullhack.event.api.handler
import love.xiguajerry.nullhack.event.impl.LoopEvent
import love.xiguajerry.nullhack.gui.hud.PlainTextHud
import love.xiguajerry.nullhack.graphics.font.TextComponent
import java.util.concurrent.CopyOnWriteArraySet

object HudEventPosts : PlainTextHud("Event Posts") {
    val events = CopyOnWriteArraySet<String>()

    private var eventsCache = emptyList<String>()

    init {
        handler<LoopEvent.Start> {
            events.clear()
        }

        handler<LoopEvent.End> {
            eventsCache = events.toList()
        }
    }

    context(TextInfo)
    override fun TextComponent.buildText() {
        eventsCache.forEach {
            addLine(it)
        }
    }
}