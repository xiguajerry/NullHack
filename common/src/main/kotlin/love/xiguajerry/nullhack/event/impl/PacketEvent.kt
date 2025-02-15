package love.xiguajerry.nullhack.event.impl

import love.xiguajerry.nullhack.event.api.*
import net.minecraft.network.protocol.Packet

sealed class PacketEvent(val packet: Packet<*>) : IEvent {
    class Send(packet: Packet<*>) : PacketEvent(packet), ICancellable by Cancellable(), IPosting by Companion {
        companion object : EventBus()
    }

    class Receive(packet: Packet<*>) : PacketEvent(packet), ICancellable by Cancellable(), IPosting by Companion {
        companion object : EventBus()
    }

    class PostSend(packet: Packet<*>) : PacketEvent(packet), IPosting by Companion {
        companion object : EventBus()
    }

    class PostReceive(packet: Packet<*>) : PacketEvent(packet), IPosting by Companion {
        companion object : EventBus()
    }
}