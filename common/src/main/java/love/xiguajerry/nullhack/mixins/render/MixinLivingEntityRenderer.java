package love.xiguajerry.nullhack.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import love.xiguajerry.nullhack.NullHackMod;
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings;
import love.xiguajerry.nullhack.modules.impl.visual.NameTags;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityRenderer.class, priority = Integer.MAX_VALUE)
public class MixinLivingEntityRenderer<T extends LivingEntity> {
    @ModifyReturnValue(method = "shouldShowName*", at = @At(value = "RETURN"))
    public boolean hasLabel$Tweaker(boolean original) {
        return original && (!NameTags.INSTANCE.isEnabled());
    }
}
