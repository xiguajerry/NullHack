package love.xiguajerry.nullhack.utils.inventory

import net.minecraft.world.inventory.Slot

class HotbarSlot(slot: Slot) : Slot(slot.container, slot.index, slot.x, slot.y) {
    init {
        index = slot.index
    }

    val hotbarSlot = slot.index
}