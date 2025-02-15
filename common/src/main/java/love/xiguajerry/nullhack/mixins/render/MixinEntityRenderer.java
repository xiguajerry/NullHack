package love.xiguajerry.nullhack.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings;
import love.xiguajerry.nullhack.modules.impl.visual.NameTags;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {
    @ModifyReturnValue(method = "shouldShowName*", at = @At(value = "RETURN"))
    public boolean hasLabel$Tweaker(boolean original) {
        return original && (!NameTags.INSTANCE.isEnabled());
    }
}
