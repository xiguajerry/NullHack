package love.xiguajerry.nullhack.event.impl.world

import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IPosting
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block

class PlaceBlockEvent(val pos: BlockPos, val block: Block) :IEvent, IPosting by PlaceBlockEvent {
    companion object : EventBus()

    fun getBlockPos(): BlockPos {
        return pos
    }

    fun getBlockState(): Block {
        return block
    }
}