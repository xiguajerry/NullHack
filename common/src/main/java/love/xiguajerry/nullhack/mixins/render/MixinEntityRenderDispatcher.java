package love.xiguajerry.nullhack.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import love.xiguajerry.nullhack.event.impl.render.RenderEntityEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderDispatcher.class, priority = Integer.MAX_VALUE)
public abstract class MixinEntityRenderDispatcher {

    @Shadow public abstract <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity);

    @Inject(method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V", at = @At("HEAD"), cancellable = true)
    public <E extends Entity, S extends EntityRenderState> void render$HEAD(
            E entity, double xOffset, double yOffset, double zOffset, float partialTick,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
            EntityRenderer<? super E, S> renderer, CallbackInfo ci
    ) {
        S entityRenderState = renderer.createRenderState(entity, partialTick);
        if (entity == null || !RenderEntityEvent.getRenderingEntities()) return;

        RenderEntityEvent eventAll = new RenderEntityEvent.All.Pre(
                entity, entityRenderState,
                poseStack, bufferSource,
                (EntityRenderer<Entity, EntityRenderState>) renderer,
                packedLight
        );
        eventAll.post();

        if (eventAll.getCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V", at = @At("RETURN"), cancellable = false)
    public <E extends Entity, S extends EntityRenderState> void render$RETURN(
            E entity, double xOffset, double yOffset, double zOffset, float partialTick,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
            EntityRenderer<? super E, S> renderer, CallbackInfo ci
    ) {
        S entityRenderState = renderer.createRenderState(entity, partialTick);
        if (entity == null || !RenderEntityEvent.getRenderingEntities()) return;

        RenderEntityEvent eventPost = new RenderEntityEvent.All.Post(
                entity, entityRenderState,
                poseStack, bufferSource,
                (EntityRenderer<Entity, EntityRenderState>) renderer,
                packedLight
        );
        eventPost.post();
    }
}
