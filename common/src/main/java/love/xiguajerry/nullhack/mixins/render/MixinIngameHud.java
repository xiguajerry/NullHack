package love.xiguajerry.nullhack.mixins.render;

import com.mojang.blaze3d.platform.GlStateManager;
import love.xiguajerry.nullhack.RenderSystem;
import love.xiguajerry.nullhack.modules.impl.visual.NoRender;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinIngameHud {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void hookRenderEvent(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        RenderSystem.INSTANCE.render2D(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
        GlStateManager._enableBlend();
        GlStateManager._disableCull();
        GlStateManager._disableDepthTest();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }


    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderPortalOverlay(GuiGraphics guiGraphics, float alpha, CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getPortal()) ci.cancel();
    }
}
