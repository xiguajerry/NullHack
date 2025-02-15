package love.xiguajerry.nullhack.mixins.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import love.xiguajerry.nullhack.RenderSystem;
import love.xiguajerry.nullhack.event.impl.render.ResolutionUpdateEvent;
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings;
import love.xiguajerry.nullhack.modules.impl.player.NoEntityTrace;
import love.xiguajerry.nullhack.modules.impl.visual.AspectRatio;
import love.xiguajerry.nullhack.modules.impl.visual.MotionBlur;
import love.xiguajerry.nullhack.modules.impl.visual.NoRender;
import love.xiguajerry.nullhack.graphics.buffer.Render3DUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.lwjgl.opengl.GL13C.glActiveTexture;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    private float zoom;
    @Shadow
    private float zoomX;
    @Shadow
    private float zoomY;

    @Shadow private float renderDistance;

    @Inject(method = "resize", at = @At("HEAD"))
    public void onResized$HEAD(int width, int height, CallbackInfo ci) {
        new ResolutionUpdateEvent(width, height).post();
        GlStateManager._glUseProgram(0);
    }

    @Inject(
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0),
            method = "renderLevel")
    private void onRenderWorld(DeltaTracker deltaTracker,
                               CallbackInfo ci,
                               @Local(ordinal = 2) Matrix4f matrix4f2,
                               @Local(ordinal = 0) float tickDelta
    ) {
        if (MotionBlur.INSTANCE.getEnable()) {
            love.xiguajerry.nullhack.graphics.shader.MotionBlur.INSTANCE.updateMatrix(
                    com.mojang.blaze3d.systems.RenderSystem.getModelViewMatrix(),
                    new Matrix4f(com.mojang.blaze3d.systems.RenderSystem.getProjectionMatrix()).mul(matrix4f2)
            );
            love.xiguajerry.nullhack.graphics.shader.MotionBlur.INSTANCE.draw();
        }
        PoseStack matrixStack = new PoseStack();
        matrixStack.mulPose(matrix4f2);
        Render3DUtils.INSTANCE.getLastProjectionMatrix().set(com.mojang.blaze3d.systems.RenderSystem.getProjectionMatrix());
        Render3DUtils.INSTANCE.getLastModelViewMatrix().set(com.mojang.blaze3d.systems.RenderSystem.getModelViewMatrix());
        Render3DUtils.INSTANCE.getLastWorldSpaceMatrix().set(matrixStack.last().pose());
        var prevTex = GlStateManager._getActiveTexture();
        RenderSystem.INSTANCE.render3D(matrixStack, tickDelta);
        GlStateManager._glUseProgram(0);
        glActiveTexture(prevTex);
        GlStateManager._enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        GlStateManager._enableCull();
        GlStateManager._enableDepthTest();
//        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
    private void tiltViewWhenHurtHook(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getHurtCam()) {
            ci.cancel();
        }
    }

    @Inject(method = "displayItemActivation", at = @At("HEAD"), cancellable = true)
    private void onShowFloatingItem(ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() == Items.TOTEM_OF_UNDYING && NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getTotem()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"))
    private float applyCameraTransformationsMathHelperLerpProxy(float delta, float first, float second) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getNausea()) return 0;
        return Mth.lerp(delta, first, second);
    }
    @Unique
    private static final Minecraft mc = Minecraft.getInstance();

    @Inject(method = "getProjectionMatrix", at = @At("TAIL"), cancellable = true)
    public void getBasicProjectionMatrixHook(float fovDegrees, CallbackInfoReturnable<Matrix4f> cir) {
        if (AspectRatio.INSTANCE.isEnabled()) {
            PoseStack matrixStack = new PoseStack();
            matrixStack.last().pose().identity();
            if (zoom != 1.0f) {
                matrixStack.translate(zoomX, -zoomY, 0.0f);
                matrixStack.scale(zoom, zoom, 1.0f);
            }
            matrixStack.last().pose().mul(new Matrix4f().setPerspective((float) (fovDegrees * (Math.PI / 180d)), AspectRatio.getRatio(), 0.05f, renderDistance * 4.0f));
            cir.setReturnValue(matrixStack.last().pose());
        }
    }

    @Inject(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"), cancellable = true)
    private void onUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        if (mc.crosshairPickEntity != null && mc.player != null
                && NoEntityTrace.INSTANCE.isEnabled()
                && (mc.player.getMainHandItem().getItem() instanceof PickaxeItem || !NoEntityTrace.INSTANCE.getPonly())
                && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            if (mc.player.getMainHandItem().getItem() instanceof SwordItem && NoEntityTrace.INSTANCE.getNoSword()) return;
            Profiler.get().pop();
            info.cancel();
        }
    }
}
