package love.xiguajerry.nullhack.mixins.world;

import love.xiguajerry.nullhack.event.impl.world.WorldEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class MixinLevel {
    @Shadow @Final public boolean isClientSide;

    @Inject(method = "onBlockStateChange", at = @At("HEAD"))
    public void onBlockChange(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci) {
        if (isClientSide) {
            var event = new WorldEvent.ClientBlockUpdate(pos, oldBlock, newBlock);
            event.post();
        }
    }
}
