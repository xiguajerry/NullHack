package love.xiguajerry.nullhack.utils.extension

import love.xiguajerry.nullhack.utils.NonNullContext
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.multiplayer.prediction.PredictiveAction

context (NonNullContext)
fun MultiPlayerGameMode.sendSequencedPacket(f: PredictiveAction) = startPrediction(world, f)