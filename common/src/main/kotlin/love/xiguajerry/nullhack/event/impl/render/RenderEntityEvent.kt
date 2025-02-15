package love.xiguajerry.nullhack.event.impl.render

import com.mojang.blaze3d.vertex.PoseStack
import love.xiguajerry.nullhack.event.api.Cancellable
import love.xiguajerry.nullhack.event.api.EventBus
import love.xiguajerry.nullhack.event.api.IEvent
import love.xiguajerry.nullhack.event.api.IPosting
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.world.entity.Entity

sealed class RenderEntityEvent(
    val entity: Entity,
) : IEvent, Cancellable() {

    abstract fun render()

    sealed class All(
        entity: Entity,
        val state: EntityRenderState,
        private val matrices: PoseStack,
        private val vertexConsumer: MultiBufferSource,
        private val renderer: EntityRenderer<Entity, EntityRenderState>,
        private val light: Int
    ) : RenderEntityEvent(entity) {

        override fun render() {
            renderer.render(state, matrices, vertexConsumer, light)
        }

        class Pre(
            entity: Entity,
            state: EntityRenderState,
            matrices: PoseStack,
            vertexConsumer: MultiBufferSource,
            renderer: EntityRenderer<Entity, EntityRenderState>,
            light: Int
        ) : All(entity, state, matrices, vertexConsumer, renderer, light), IPosting by Companion {
            companion object : EventBus()
        }

        class Post(
            entity: Entity,
            state: EntityRenderState,
            matrices: PoseStack,
            vertexConsumer: MultiBufferSource,
            renderer: EntityRenderer<Entity, EntityRenderState>,
            light: Int
        ) : All(entity, state, matrices, vertexConsumer, renderer, light), IPosting by Companion {
            companion object : EventBus()
        }
    }

    companion object {
        @JvmStatic
        var renderingEntities = false
    }
}