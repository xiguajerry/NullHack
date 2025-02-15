package love.xiguajerry.nullhack.event.impl.render

import com.mojang.blaze3d.vertex.PoseStack
import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting

class Render3DEvent(val matrixStack: PoseStack, val partialTicks: Float) : IEvent, IPosting by Companion {
    companion object : EventBus()
}