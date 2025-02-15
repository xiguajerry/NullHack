package love.xiguajerry.nullhack.modules.impl.combat

import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.UpdateEvent
import love.xiguajerry.nullhack.event.impl.player.OnUpdateWalkingPlayerEvent
import love.xiguajerry.nullhack.gui.NullClickGui
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.utils.NonNullContext
import love.xiguajerry.nullhack.utils.inventory.InventoryUtils.doSwap
import love.xiguajerry.nullhack.utils.timing.TickTimer
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

object AutoTotem : Module("Auto Totem", category = Category.COMBAT) {
    private val mainHand by setting("MainHand", false)
    private val health by setting("Health", 0.1, 0.0..36.0)

    private val timer = TickTimer()

    init {
        nonNullHandler<OnUpdateWalkingPlayerEvent.Pre> { update() }
        nonNullHandler<UpdateEvent> { update() }
    }

    context(NonNullContext)
    private fun findItemInventorySlot(item: Item): Int {
        for (i in 0..44) {
            val stack = player.inventory.getItem(i)
            if (stack.item == item) return if (i < 9) i + 36 else i
        }
        return -1
    }

    context(NonNullContext)
     fun update() {
        if (mc.screen != null && mc.screen !is ChatScreen && mc.screen !is InventoryScreen
            && mc.screen !is NullClickGui) return
        if (!timer.tick(200)) return
        if (player.health + player.absorptionAmount > health) return
        if (player.mainHandItem.item == Items.TOTEM_OF_UNDYING
            || player.mainHandItem.item == Items.TOTEM_OF_UNDYING) return
        val itemSlot: Int = findItemInventorySlot(Items.TOTEM_OF_UNDYING)
        if (itemSlot != -1) {
            if (mainHand) {
               doSwap(0)
                if (player.inventory.getItem(0).item !=Items.TOTEM_OF_UNDYING) {
                    interaction.handleInventoryMouseClick(player.containerMenu.containerId, itemSlot, 0, ClickType.PICKUP, player)
                    interaction.handleInventoryMouseClick(player.containerMenu.containerId, 36, 0, ClickType.PICKUP, player)
                    interaction.handleInventoryMouseClick(player.containerMenu.containerId, itemSlot, 0, ClickType.PICKUP, player)
                    netHandler.send(ServerboundContainerClosePacket(player.containerMenu.containerId))
                }
            } else {
                interaction.handleInventoryMouseClick(player.containerMenu.containerId, itemSlot, 0, ClickType.PICKUP, player)
                interaction.handleInventoryMouseClick(player.containerMenu.containerId, 45, 0, ClickType.PICKUP, player)
                interaction.handleInventoryMouseClick(player.containerMenu.containerId, itemSlot, 0, ClickType.PICKUP, player)
                netHandler.send(ServerboundContainerClosePacket(player.containerMenu.containerId))
            }
            timer.reset()
        }
    }
}