package love.xiguajerry.nullhack.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import love.xiguajerry.nullhack.modules.impl.visual.NoRender;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinArmorFeatureRenderer<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> extends RenderLayer<S, M> {

    public MixinArmorFeatureRenderer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void onRenderArmor(PoseStack poseStack, MultiBufferSource bufferSource, ItemStack armorItem, EquipmentSlot slot, int packedLight, A model, CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getArmor()) {
            ci.cancel();
        }
    }
}