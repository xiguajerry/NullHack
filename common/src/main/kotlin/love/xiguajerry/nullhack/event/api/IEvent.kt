package love.xiguajerry.nullhack.event.api

import love.xiguajerry.nullhack.gui.hud.impl.HudEventPosts

interface IEvent : IPosting {
    fun post() {
        HudEventPosts.events.add(this::class.java.name)
        post(this)
    }
}