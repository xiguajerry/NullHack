package love.xiguajerry.nullhack.event.impl.player

import love.xiguajerry.nullhack.event.api.*
import net.minecraft.client.player.ClientInput

class InputUpdateEvent(val movementInput: ClientInput) : IEvent, IPosting by Companion, ICancellable by Cancellable() {
    companion object : EventBus()
}