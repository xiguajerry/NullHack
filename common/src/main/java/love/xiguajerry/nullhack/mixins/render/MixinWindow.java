package love.xiguajerry.nullhack.mixins.render;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import love.xiguajerry.nullhack.NullHackMod;
import love.xiguajerry.nullhack.RenderSystem;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.*;

@Mixin(Window.class)
public class MixinWindow {
    @Shadow @Final private long window;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 5, shift = At.Shift.AFTER, remap = false))
    public void hookInit(WindowEventHandler eventHandler, ScreenManager screenManager, DisplayData displayData, String preferredFullscreenVideoMode, String title, CallbackInfo ci) {
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 0);
        glfwWindowHint(GLFW_CONTEXT_DEBUG, 1);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void hookInit$Return(WindowEventHandler eventHandler, ScreenManager screenManager, DisplayData displayData, String preferredFullscreenVideoMode, String title, CallbackInfo ci) {
        // hacky method to move the window to the top of the screen
        glfwSetWindowAttrib(window, GLFW_FOCUS_ON_SHOW, 1);
        glfwShowWindow(window);
        glfwSetWindowAttrib(window, GLFW_FLOATING, 1);
        glfwSetWindowAttrib(window, GLFW_FLOATING, 0);
        glfwFocusWindow(window);
    }

    @Redirect(method = "setTitle", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowTitle(JLjava/lang/CharSequence;)V", remap = false))
    public void onSetWindowTitle(long window, CharSequence title) {
        GLFW.glfwSetWindowTitle(window, title + " " + NullHackMod.CLIENT_TYPE);
    }
}
