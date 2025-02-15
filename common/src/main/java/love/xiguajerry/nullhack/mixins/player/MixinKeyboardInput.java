package love.xiguajerry.nullhack.mixins.player;

import love.xiguajerry.nullhack.event.impl.player.InputUpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput {
    @Inject(method = "tick", at = @At(value = "RETURN"), cancellable = true)
    private void onSneak(CallbackInfo ci) {
        InputUpdateEvent event = new InputUpdateEvent(Minecraft.getInstance().player.input);
        event.post();
        if (event.getCancelled()) ci.cancel();
    }
}
