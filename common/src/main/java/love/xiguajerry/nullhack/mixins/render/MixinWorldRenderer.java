package love.xiguajerry.nullhack.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import love.xiguajerry.nullhack.event.impl.render.RenderEntityEvent;
import love.xiguajerry.nullhack.graphics.shader.MotionBlur;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(value = LevelRenderer.class, priority = Integer.MAX_VALUE)
public abstract class MixinWorldRenderer {
    @Shadow private @Nullable ClientLevel level;

    @Shadow protected abstract boolean shouldShowEntityOutlines();

    @Inject(method = "renderEntities", at = @At(value = "HEAD"))
    private void on$Profiler$SwapToEntities(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, Camera camera, DeltaTracker deltaTracker, List<Entity> entities, CallbackInfo ci) {
//        new Throwable().printStackTrace();
//        NullHackMod.INSTANCE.getLOGGER().info(this.world.getProfiler().toString());
        RenderEntityEvent.setRenderingEntities(true);
    }

    @Inject(method = "renderEntities", at = @At(value = "RETURN"))
    private void on$Profiler$SwapToBlockEntities(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, Camera camera, DeltaTracker deltaTracker, List<Entity> entities, CallbackInfo ci) {
//        new Throwable().printStackTrace();
        RenderEntityEvent.setRenderingEntities(false);
    }

    @Inject(method = "renderSectionLayer", at = @At(value = "HEAD"))
    private void onRenderLayer(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (renderType == RenderType.translucent()) {
            MotionBlur.INSTANCE.copyDepthBuffer();
        }
    }

//    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V", ordinal = 0))
//    private void replaceShaderHook(PostEffectProcessor instance, float tickDelta) {
//        if (Shaders.INSTANCE.isEnabled()) Shaders.INSTANCE.applyShader(
//                Objects.requireNonNull(((WorldRenderer) (Object) this).getEntityOutlinesFramebuffer())
//        );
//    }
}
