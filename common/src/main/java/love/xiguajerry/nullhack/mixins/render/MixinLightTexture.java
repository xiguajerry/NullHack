package love.xiguajerry.nullhack.mixins.render;

import love.xiguajerry.nullhack.modules.impl.visual.FullBright;
import love.xiguajerry.nullhack.modules.impl.visual.NoRender;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightTexture.class)
public class MixinLightTexture {
    @ModifyArgs(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/shaders/AbstractUniform;set(Lorg/joml/Vector3f;)V"))
    private void update(Args args) {
        if (FullBright.INSTANCE.isEnabled()) {
            var vec = new Vector3f(FullBright.INSTANCE.getRgb().getRed(), FullBright.INSTANCE.getRgb().getGreen(), FullBright.INSTANCE.getRgb().getBlue());
            args.set(0, vec);
        }
    }

    @Inject(method = "getDarknessGamma", at = @At("HEAD"), cancellable = true)
    private void getDarknessFactor(float tickDelta, CallbackInfoReturnable<Float> info) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getDarkness()) info.setReturnValue(0.0f);
    }
}
