package love.xiguajerry.nullhack.mixins.render;

import love.xiguajerry.nullhack.modules.impl.visual.NoRender;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class MixinBackgroundRenderer {
    @Inject(method = "setupFog", at = @At("TAIL"), cancellable = true)
    private static void onApplyFog(Camera camera, FogRenderer.FogMode fogMode, Vector4f fogColor, float renderDistance, boolean isFoggy, float partialTick, CallbackInfoReturnable<FogParameters> cir) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getFog()) {
            if (fogMode == FogRenderer.FogMode.FOG_TERRAIN) {
                cir.setReturnValue(FogParameters.NO_FOG);
            }
        }
    }

    @Inject(method = "getPriorityFogFunction", at = @At("HEAD"), cancellable = true)
    private static void onGetFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<Object> info) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getBlindness()) info.setReturnValue(null);
    }
}
