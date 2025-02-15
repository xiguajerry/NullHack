package love.xiguajerry.nullhack.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import love.xiguajerry.nullhack.event.impl.player.PlayerUpdateVelocityEvent;
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings;
import love.xiguajerry.nullhack.modules.impl.visual.NameTags;
import love.xiguajerry.nullhack.modules.impl.visual.NoRender;
import love.xiguajerry.nullhack.modules.impl.visual.Shaders;
import love.xiguajerry.nullhack.utils.MinecraftWrapper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import love.xiguajerry.nullhack.modules.impl.movement.Velocity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantValue")
@Mixin(value = Entity.class, priority = Integer.MAX_VALUE)
public abstract class MixinEntity {

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void applyEntityCollisionHead(Entity entityIn, CallbackInfo ci) {
        Velocity.handleApplyEntityCollision((Entity) (Object) this, entityIn, ci);
    }

    @Inject(method = "isOnFire", at = @At("HEAD"), cancellable = true)
    void isOnFireHook(CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getFireEntity()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static Vec3 nullhack_nextgen$movementInputToVelocity(Vec3 movementInput, float speed, float yaw) {
        double d = movementInput.lengthSqr();
        if (d < 1.0E-7) {
            return Vec3.ZERO;
        }
        Vec3 Vec3 = (d > 1.0 ? movementInput.normalize() : movementInput).scale(speed);
        float f = Mth.sin(yaw * ((float) Math.PI / 180));
        float g = Mth.cos(yaw * ((float) Math.PI / 180));
        return new Vec3(Vec3.x * (double) g - Vec3.z * (double) f, Vec3.y, Vec3.z * (double) g + Vec3.x * (double) f);
    }

    @Inject(method = "moveRelative", at = {@At("HEAD")}, cancellable = true)
    public void updateVelocityHook(float speed, Vec3 movementInput, CallbackInfo ci) {
        var mc = MinecraftWrapper.getMc();
        if ((Object) this == (Object) mc.player) {
            var event = new PlayerUpdateVelocityEvent(movementInput, speed, mc.player.getYRot(),
                    nullhack_nextgen$movementInputToVelocity(movementInput, speed, mc.player.getYRot()));
            event.post();
            if (event.getCancelled()) {
                ci.cancel();
                mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(event.getVelocity()));
            }
        }
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    public void isGlowingHook(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(Shaders.INSTANCE.shouldRender((Entity)(Object)this));
    }

    @ModifyReturnValue(method = "shouldShowName", at = @At(value = "RETURN"))
    public boolean shouldRenderName$Tweaker(boolean original) {
        return original && (!NameTags.INSTANCE.isEnabled());
    }
}