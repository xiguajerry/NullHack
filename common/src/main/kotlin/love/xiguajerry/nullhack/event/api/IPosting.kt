package love.xiguajerry.nullhack.event.api


interface IPosting {
    val eventBus: EventBus

    fun post(event: Any)
}


