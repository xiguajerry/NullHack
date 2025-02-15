package love.xiguajerry.nullhack.manager.managers

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import love.xiguajerry.nullhack.event.api.AlwaysListening
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.PacketEvent
import love.xiguajerry.nullhack.event.impl.player.HotbarUpdateEvent
import love.xiguajerry.nullhack.manager.AbstractManager
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.NonNullContext
import love.xiguajerry.nullhack.utils.extension.isHotbarSlot
import love.xiguajerry.nullhack.utils.inventory.SlotRanges
import love.xiguajerry.nullhack.utils.inventory.hotbarSlots
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

object HotbarSwitchManager : AbstractManager(), AlwaysListening {
    var serverSideHotbar = 0; private set
    var swapTime = 0L; private set

    val Player.serverSideItem: ItemStack
        get() = hotbarSlots[serverSideHotbar].item

    init {
        nonNullHandler<PacketEvent.Send>(Int.MIN_VALUE) {
            val packet = it.packet
            if (it.cancelled || packet !is ServerboundSetCarriedItemPacket) return@nonNullHandler

            val prev: Int

            synchronized(InventoryManager) {
                prev = serverSideHotbar
                serverSideHotbar = packet.slot
                swapTime = System.currentTimeMillis()
            }

            if (prev != it.packet.slot) {
                HotbarUpdateEvent(prev, serverSideHotbar).post()
            }
        }
    }

    context (NonNullContext)
    fun ghostSwitch(slot: Slot, block: () -> Unit) {
        ghostSwitch(Override.DEFAULT, slot, block)
    }

    context (NonNullContext)
    fun ghostSwitch(slot: Int, block: () -> Unit) {
        ghostSwitch(Override.DEFAULT, slot, block)
    }

    context (NonNullContext)
    fun ghostSwitch(override: Override, slot: Slot, block: () -> Unit) {
        synchronized(InventoryManager) {
            if (slot.index != serverSideHotbar) {
                override.mode.run {
                    switch(slot, block)
                }
                return
            }
        }
        block.invoke()
    }

    context(NonNullContext)
    fun ghostSwitchc( slot: Int, block: () -> Unit) {
      val  i =  player.inventory.selected
        doSwap(slot)
        block.invoke()
        doSwap(i)
    }
    context(NonNullContext)
    fun doSwap(slot: Int) {
        player.inventory.selected = slot
        netHandler.send(ServerboundSetCarriedItemPacket(slot))
    }

    context (NonNullContext)
    fun ghostSwitch(override: Override, slot: Int, block: () -> Unit) {
        ghostSwitch(override, player.inventoryMenu.getSlot(slot), block)
    }

    enum class BypassMode : Displayable {
        NONE {
            override fun NonNullContext.switch(targetSlot: Slot, block: () -> Unit) {
                if (!targetSlot.isHotbarSlot && targetSlot.index !in 36..45) {
                    SWAP.run {
                        switch(targetSlot, block)
                        return
                    }
                }

                val targetId = if (targetSlot.isHotbarSlot) targetSlot.index else targetSlot.index - 36
                val prevSlot = serverSideHotbar
                player.inventory.selected = targetId
                netHandler.send(ServerboundSetCarriedItemPacket(targetId))
                block.invoke()
                player.inventory.selected = prevSlot
                netHandler.send(ServerboundSetCarriedItemPacket(prevSlot))
            }
        },
        MOVE {
            override fun NonNullContext.switch(targetSlot: Slot, block: () -> Unit) {
                val hotbarSlots = player.hotbarSlots
                val inventoryContainer = player.containerMenu
                val targetItem = targetSlot.item

                netHandler.send(
                    ServerboundContainerClickPacket(
                        inventoryContainer.containerId,
                        inventoryContainer.incrementStateId(),
                        targetSlot.index,
                        0,
                        ClickType.PICKUP,
                        targetItem,
                        Int2ObjectArrayMap(mapOf(targetSlot.index to targetSlot.item))
                    )
                )
                netHandler.send(
                    ServerboundContainerClickPacket(
                        inventoryContainer.containerId,
                        inventoryContainer.incrementStateId(),
                        hotbarSlots[serverSideHotbar].index,
                        0,
                        ClickType.PICKUP,
                        player.serverSideItem,
                        Int2ObjectArrayMap(mapOf(hotbarSlots[serverSideHotbar].index to hotbarSlots[serverSideHotbar].item))
                    )
                )

                block.invoke()

                netHandler.send(
                    ServerboundContainerClickPacket(
                        inventoryContainer.containerId,
                        inventoryContainer.incrementStateId(),
                        hotbarSlots[serverSideHotbar].index,
                        0,
                        ClickType.PICKUP,
                        targetItem,
                        Int2ObjectArrayMap(mapOf(hotbarSlots[serverSideHotbar].index to hotbarSlots[serverSideHotbar].item))
                    )
                )
                netHandler.send(
                    ServerboundContainerClickPacket(
                        inventoryContainer.containerId,
                        inventoryContainer.incrementStateId(),
                        targetSlot.index,
                        0,
                        ClickType.PICKUP,
                        ItemStack.EMPTY,
                        Int2ObjectArrayMap(mapOf(targetSlot.index to ItemStack.EMPTY))
                    )
                )
            }
        },
        SWAP {
            override fun NonNullContext.switch(targetSlot: Slot, block: () -> Unit) {
                if (ClientSettings.inventorySwapBypass) {
                    interaction.handleInventoryMouseClick(player.containerMenu.containerId, targetSlot.index, 0,
                        ClickType.PICKUP, player)
                } else {
                    interaction.handleInventoryMouseClick(player.containerMenu.containerId,
                        targetSlot.index, 0, ClickType.SWAP, player)
                }
                block.invoke()
                if (ClientSettings.inventorySwapBypass) {
                    interaction.handleInventoryMouseClick(player.containerMenu.containerId, targetSlot.index, 0, ClickType.PICKUP, player)
                } else {
                    interaction.handleInventoryMouseClick(player.containerMenu.containerId,
                        targetSlot.index, 0, ClickType.SWAP, player)
                }
            }
        },
        PICK {
            override fun NonNullContext.switch(targetSlot: Slot, block: () -> Unit) {
                if (targetSlot.index == SlotRanges.OFFHAND || targetSlot.index !in SlotRanges.HOTBAR) {
                    SWAP.run {
                        switch(targetSlot, block)
                        return
                    }
                }
                val number = targetSlot.index
                interaction.handleInventoryMouseClick(player.containerMenu.containerId, number, 0, ClickType.PICKUP, player)
                block.invoke()
                interaction.handleInventoryMouseClick(player.containerMenu.containerId, number, 0, ClickType.PICKUP, player)
            }
        };

        abstract fun NonNullContext.switch(targetSlot: Slot, block: () -> Unit)
    }

    enum class Override : Displayable {
        DEFAULT {
            override val mode get() = ClientSettings.ghostHandBypass as BypassMode
        },
        NONE {
            override val mode = BypassMode.NONE
        },
        MOVE {
            override val mode = BypassMode.MOVE
        },
        SWAP {
            override val mode = BypassMode.SWAP
        },
        PICK {
            override val mode = BypassMode.PICK
        };

        abstract val mode: BypassMode
    }
}