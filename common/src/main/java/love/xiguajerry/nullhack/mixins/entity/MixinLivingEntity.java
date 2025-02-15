package love.xiguajerry.nullhack.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import love.xiguajerry.nullhack.event.impl.player.PlayerJumpEvent;
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings;
import love.xiguajerry.nullhack.modules.impl.visual.NameTags;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    @ModifyReturnValue(method = "shouldShowName", at = @At(value = "RETURN"))
    public boolean shouldRenderName$Tweaker(boolean original) {
        return original && (!NameTags.INSTANCE.isEnabled());
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void onJumpPre(CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            if (player != Minecraft.getInstance().player)
                return;
            PlayerJumpEvent.Pre.INSTANCE.post();
        }
    }

    @Inject(method = "jumpFromGround", at = @At("RETURN"))
    private void onJumpPost(CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            if (player != Minecraft.getInstance().player)
                return;
            PlayerJumpEvent.Post.INSTANCE.post();
        }
    }
}
