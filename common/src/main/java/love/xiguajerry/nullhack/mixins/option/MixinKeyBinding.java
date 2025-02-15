package love.xiguajerry.nullhack.mixins.option;

import love.xiguajerry.nullhack.manager.managers.EntityMovementManager;
import love.xiguajerry.nullhack.modules.impl.movement.SafeWalk;
import love.xiguajerry.nullhack.modules.impl.player.Scaffold;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public abstract class MixinKeyBinding {

    @Inject(method = "isDown",at = @At("HEAD"),cancellable = true)
    private void pressHook(CallbackInfoReturnable<Boolean> cir){
        if(this.equals(Minecraft.getInstance().options.keyShift)
                && Minecraft.getInstance().player != null
                && Minecraft.getInstance().level != null
                && (SafeWalk.INSTANCE.getEnable() && SafeWalk.INSTANCE.getEagle())
                && Minecraft.getInstance().player.onGround()
                && Minecraft.getInstance().level.getBlockState(
                        new BlockPos((int) Math.floor(Minecraft.getInstance().player.getX()),
                                (int) Math.floor(Minecraft.getInstance().player.getY()) - 1,
                                (int) Math.floor(Minecraft.getInstance().player.getZ()))).isAir()
                && !EntityMovementManager.INSTANCE.isSafeWalk()
                && !Scaffold.INSTANCE.getShouldSafeWalk()){
            cir.setReturnValue(true);
        }
    }
}