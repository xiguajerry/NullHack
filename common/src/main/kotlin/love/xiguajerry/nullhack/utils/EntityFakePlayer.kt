@file:OptIn(ExperimentalStdlibApi::class)

package love.xiguajerry.nullhack.utils


import com.mojang.authlib.GameProfile
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.RemotePlayer
import java.util.*

class EntityFakePlayer(world: ClientLevel, name: String, uuid: UUID? = null) : RemotePlayer(world, GameProfile(adaptUUID(uuid), name)) {
    companion object {
        fun adaptUUID(uuid: UUID?): UUID {
            var ret = uuid
            if (ClientSettings.customUUID)
                ret = runCatching { UUID.fromString(ClientSettings.uuid) }
                    .getOrElse {

                        it.printStackTrace()
                        UUID.randomUUID()
                    }
            return ret ?: UUID.randomUUID()
        }
    }
}