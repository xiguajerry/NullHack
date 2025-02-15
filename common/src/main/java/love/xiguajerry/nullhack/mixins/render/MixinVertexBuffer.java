package love.xiguajerry.nullhack.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.ShaderProgram;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VertexBuffer.class)
public abstract class MixinVertexBuffer {
    @Shadow private VertexFormat.Mode mode;

    @Shadow public abstract void draw();

    @Shadow public abstract void bind();

    /**
     * @author SagiriXiguajerry
     * @reason bind buffer explicitly
     */
    @Overwrite
    public void drawWithShader(Matrix4f viewMatrix, Matrix4f projectionMatrix, CompiledShaderProgram program) {
        if (program != null) {
            RenderSystem.assertOnRenderThread();
            program.setDefaultUniforms(this.mode, viewMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
            program.apply();
            this.bind();
            this.draw();
            program.clear();
        }
    }
}
