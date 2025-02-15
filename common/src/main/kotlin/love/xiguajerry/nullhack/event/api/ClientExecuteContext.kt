package love.xiguajerry.nullhack.event.api

import love.xiguajerry.nullhack.command.CommandManager
import love.xiguajerry.nullhack.command.execute.ExecuteEvent
import love.xiguajerry.nullhack.command.execute.IExecuteEvent
import love.xiguajerry.nullhack.utils.ClientContext
import love.xiguajerry.nullhack.utils.NonNullContext
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.player.LocalPlayer

fun ClientExecuteContext.toSafe() =
    if (world != null && player != null && interaction != null && netHandler != null) NonNullExecuteContext(
        world,
        player,
        interaction,
        netHandler,
        this
    )
    else null

class ClientExecuteContext(
    args: Array<String>
) : ClientContext(), IExecuteEvent by ExecuteEvent(CommandManager, args)

class NonNullExecuteContext internal constructor(
    world: ClientLevel,
    player: LocalPlayer,
    interaction: MultiPlayerGameMode,
    connection: ClientPacketListener,
    event: ClientExecuteContext
) : NonNullContext(world, player, interaction, connection), IExecuteEvent by event