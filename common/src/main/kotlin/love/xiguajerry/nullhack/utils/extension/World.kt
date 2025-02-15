package love.xiguajerry.nullhack.utils.extension

import love.xiguajerry.nullhack.mixins.accessor.IClientLevelAccessor
import love.xiguajerry.nullhack.utils.NonNullContext
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

fun Level.checkBlockCollision(bb: AABB): Boolean {
    val j2 = Mth.floor(bb.minX)
    val k2 = Mth.ceil(bb.maxX)
    val l2 = Mth.floor(bb.minY)
    val i3 = Mth.ceil(bb.maxY)
    val j3 = Mth.floor(bb.minZ)
    val k3 = Mth.ceil(bb.maxZ)
    val mutable = BlockPos.MutableBlockPos()
    for (l3 in j2..<k2) {
        for (i4 in l2..<i3) {
            for (j4 in j3..<k3) {
                val state: BlockState = this.getBlockState(mutable.set(l3, i4, j4))
                if (state.isAir) {
                    return true
                }
            }
        }
    }
    return false
}

context(NonNullContext)
inline fun Level.getActionId(): Int {
    val pendingUpdateManager = (world as IClientLevelAccessor).acquirePendingUpdateManager()
    val actionId = pendingUpdateManager.currentSequence()
    pendingUpdateManager.close()
    return actionId
}