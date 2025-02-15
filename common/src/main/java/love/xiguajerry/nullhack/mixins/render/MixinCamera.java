package love.xiguajerry.nullhack.mixins.render;

import love.xiguajerry.nullhack.modules.impl.visual.NoCameraClip;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Inject(at = @At("HEAD"), method = "getMaxZoom(F)F", cancellable = true)
    private void onClipToSpace(float desiredCameraDistance,
                               CallbackInfoReturnable<Float> cir)
    {
        if(NoCameraClip.INSTANCE.isEnabled())
            cir.setReturnValue(desiredCameraDistance);
    }
}