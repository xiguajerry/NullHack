package love.xiguajerry.nullhack.utils.extension

import love.xiguajerry.nullhack.utils.inventory.HotbarSlot
import love.xiguajerry.nullhack.utils.inventory.SlotRanges
import net.minecraft.world.inventory.Slot

val Slot.isHotbarSlot: Boolean
    get() = this.index in SlotRanges.HOTBAR

val Slot.hotbarIndex get() = if (isHotbarSlot) this.index else -1

fun Slot.toHotbarSlotOrNull() =
    if (isHotbarSlot) HotbarSlot(this)
    else null