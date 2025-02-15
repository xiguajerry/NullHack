package love.xiguajerry.nullhack.mixins.accessor;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author CuteMic
 */
@Mixin(ClientboundSetEntityMotionPacket.class)
public interface IEntityVelocityUpdateS2CPacketAccessor {

    @Mutable
    @Accessor("xa")
    void setX(int velocityX);

    @Mutable
    @Accessor("ya")
    void setY(int velocityY);

    @Mutable
    @Accessor("za")
    void setZ(int velocityZ);
}
