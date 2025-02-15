package love.xiguajerry.nullhack.modules.impl.client

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.PacketEvent
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.ChatUtils
import net.minecraft.network.protocol.common.ClientboundPingPacket
import net.minecraft.network.protocol.common.ServerboundPongPacket
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket

object PacketDebug : Module("Packet Debug", category = Category.CLIENT) {
    private val ping by setting("Ping", false)
    private val pong by setting("Pong", false)
    private val allPacketNameSpace by setting("All Packet Namespace",false)
    private val entityVelocityUpdateS2CPacket by setting("Self EntityVelocityUpdateS2C",false)
    private val calcPingAverage by setting("Calc Ping Average",true, { ping })
    private val flagCheck by setting("Flag Check",false)
    private val translationPacketNameSpace by setting("Translation Receive Namespace",true)
    private val translationValue by setting("Translation","minecraft:ping",{ translationPacketNameSpace })

    private var lastTime = 0L

    init {

        onDisabled {
            lastTime = System.currentTimeMillis()
        }

        nonNullHandler<PacketEvent.Send> {
            if (it.packet is ServerboundPongPacket && pong){
                ChatUtils.sendMessage("CommonPongC2SPacket.parameter=${it.packet.id}")
            }
        }

        nonNullHandler<PacketEvent.Receive> {

            if (flagCheck && it.packet is ClientboundMoveEntityPacket.PosRot){
                ChatUtils.sendMessage("Flag rotate and move relative")
            }

            if (flagCheck && it.packet is ClientboundRotateHeadPacket){
                ChatUtils.sendMessage("Flag set head yaw")
            }

            if (allPacketNameSpace){
                ChatUtils.sendMessage(it.packet.type().id)
            }

            if (translationPacketNameSpace){

                if (it.packet.type().id.toString() == translationValue){
                    ChatUtils.sendMessage(it.packet)
                }

            }

            if (it.packet is ClientboundPingPacket && ping){
                ChatUtils.sendMessage("CommonPingS2CPacket.parameter=${it.packet.id}")

                if (calcPingAverage){

                    ChatUtils.sendMessage("Ping Average: ${System.currentTimeMillis() - lastTime}ms")

                    lastTime = System.currentTimeMillis()
                }
            }

            if (it.packet is ClientboundSetEntityMotionPacket && it.packet.id == player.id && entityVelocityUpdateS2CPacket){
                ChatUtils.sendMessage("EntityVelocityUpdateS2C: eid=${it.packet.id}, velocityX=${it.packet.xa}, velocityY=${it.packet.ya}, velocityZ=${it.packet.za}, skip_byError=${it.packet.isSkippable}")
            }
        }
    }
}