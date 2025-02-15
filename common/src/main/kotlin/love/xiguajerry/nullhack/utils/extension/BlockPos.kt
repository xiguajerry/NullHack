package love.xiguajerry.nullhack.utils.extension

import love.xiguajerry.nullhack.utils.NonNullContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos

context (NonNullContext)
val BlockPos.state: BlockState
    get() = world.getBlockState(this)

context (NonNullContext)
val BlockPos.block: Block
    get() = state.block

context (NonNullContext)
val BlockPos.isAir: Boolean
    get() = this.block == Blocks.AIR