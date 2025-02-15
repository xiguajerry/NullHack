package love.xiguajerry.nullhack.mixins

import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.platform.FramerateLimitTracker
import com.mojang.blaze3d.platform.Window
import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.NullHackMod.initialize
import love.xiguajerry.nullhack.NullHackMod.initializePre
import love.xiguajerry.nullhack.NullHackMod.postInitialize
import love.xiguajerry.nullhack.NullHackMod.profiler
import love.xiguajerry.nullhack.RenderSystem
import love.xiguajerry.nullhack.event.impl.LoopEvent
import love.xiguajerry.nullhack.event.impl.TickEvent
import love.xiguajerry.nullhack.event.impl.world.WorldEvent
import love.xiguajerry.nullhack.manager.managers.ConfigManager.save
import love.xiguajerry.nullhack.manager.managers.ProcessExitHook.onExit
import love.xiguajerry.nullhack.modules.impl.player.MultiTask
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.ReceivingLevelScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.main.GameConfig
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.particle.ParticleEngine
import net.minecraft.client.player.LocalPlayer
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.lwjgl.opengl.GL45
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Suppress("FunctionName", "unused")
@Mixin(Minecraft::class)
abstract class MixinMinecraft {
    @field:Shadow
    @JvmField
    var player: LocalPlayer? = TODO()

    @field:Shadow
    @JvmField
    var missTime: Int = TODO()

    @field:Shadow
    @JvmField
    var hitResult: HitResult? = TODO()

    @field:Shadow
    @JvmField
    var level: ClientLevel? = TODO()

    @field:Shadow
    @JvmField
    var gameMode: MultiPlayerGameMode? = TODO()

    @field:Final
    @field:Shadow
    @JvmField
    var particleEngine: ParticleEngine = TODO()

    @field:Shadow
    @JvmField
    var metricsRecorder: MetricsRecorder = TODO()

    @Redirect(method = ["<init>"], at = At(value = "INVOKE", target = "Ljava/lang/System;currentTimeMillis()J"))
    @Throws(InterruptedException::class)
    fun onInitBackendSystem(): Long {
        return System.currentTimeMillis()
    }

    @Inject(
        method = ["run"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiling/metrics/profiling/MetricsRecorder;startTick()V"
        )]
    )
    fun onLoopStart(ci: CallbackInfo) {
        profiler.clear()
        LoopEvent.Start.post()
    }

    @Inject(method = ["run"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTick(Z)V")])
    fun onRender(ci: CallbackInfo) {
        LoopEvent.Render.post()
    }

    @Inject(
        method = ["run"],
        at = [At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTick(Z)V", shift = At.Shift.AFTER)]
    )
    fun onRenderPost(ci: CallbackInfo) {
        LoopEvent.RenderPost.post()
    }

    @Inject(method = ["runTick"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;tick()V")])
    fun onTick(tick: Boolean, ci: CallbackInfo) {
        LoopEvent.Tick.post()
    }

    @Redirect(
        method = ["runTick"],
        at = At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/FramerateLimitTracker;getFramerateLimit()I")
    )
    fun render_updateFpsLimit(instance: FramerateLimitTracker?): Int {
        return RenderSystem.getCurrentMaxFps(Minecraft.getInstance().options.framerateLimit().get())
    }

    @Redirect(
        method = ["runTick"],
        at = At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;blitToScreen(II)V")
    )
    fun onBlitFramebuffer(instance: RenderTarget, width: Int, height: Int) {
        GL45.glBlitNamedFramebuffer(
            instance.frameBufferId, 0,
            0, 0, width, height,
            0, 0, RenderSystem.width, RenderSystem.height,
            GL45.GL_COLOR_BUFFER_BIT, GL45.GL_NEAREST
        )
    }

    @Redirect(method = ["runTick"], at = At(value = "INVOKE", target = "Ljava/lang/Thread;yield()V"))
    fun onThreadYield() {
    }

    @Inject(method = ["tick"], at = [At("HEAD")])
    private fun onPreTick(info: CallbackInfo?) {
        metricsRecorder.profiler.push(NullHackMod.ID + "_pre_update")
        TickEvent.Pre.post()
        metricsRecorder.profiler.pop()
    }

    @Inject(method = ["tick"], at = [At("TAIL")])
    private fun onTick(info: CallbackInfo?) {
        metricsRecorder.profiler.push(NullHackMod.ID + "_pre_update")
        TickEvent.Post.post()
        metricsRecorder.profiler.pop()
    }

    @Inject(
        method = ["run"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiling/metrics/profiling/MetricsRecorder;endTick()V"
        )]
    )
    fun onLoopEnd(ci: CallbackInfo) {
        LoopEvent.End.post()
//        System.out.println(NullHackMod.INSTANCE.getProfiler());
    }

    @Inject(method = ["setLevel"], at = [At("HEAD")])
    fun onJoinWorld(level: ClientLevel, reason: ReceivingLevelScreen.Reason, ci: CallbackInfo) {
        WorldEvent.Load.post()
    }

    @Inject(method = ["continueAttack"], at = [At("HEAD")], cancellable = true)
    private fun handleBlockBreaking(breaking: Boolean, ci: CallbackInfo) {
        if (this.missTime <= 0 && this.player!!.isUsingItem && MultiTask.isEnabled) {
            if (breaking && this.hitResult != null && this.hitResult?.type == HitResult.Type.BLOCK) {
                val blockHitResult = this.hitResult as BlockHitResult
                val blockPos = blockHitResult.blockPos
                if (!this.level!!.getBlockState(blockPos).isAir) {
                    val direction = blockHitResult.direction
                    if (this.gameMode!!.continueDestroyBlock(blockPos, direction)) {
                        this.particleEngine.crack(blockPos, direction)
                        this.player!!.swing(InteractionHand.MAIN_HAND)
                    }
                }
            } else {
                this.gameMode!!.stopDestroyBlock()
            }
            ci.cancel()
        }
    }


    @Inject(
        method = ["disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;)V"
        )]
    )
    fun onUnloadWorld(nextScreen: Screen, keepResourcePacks: Boolean, ci: CallbackInfo) {
        WorldEvent.Unload.post()
    }

    @Inject(
        method = ["<init>"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/thread/ReentrantBlockableEventLoop;<init>(Ljava/lang/String;)V",
            shift = At.Shift.AFTER
        )]
    )
    private fun onInit_Pre(gameConfig: GameConfig, ci: CallbackInfo) {
        initializePre()
    }

    @Inject(
        method = ["<init>"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;createReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/server/packs/resources/ReloadInstance;",
            shift = At.Shift.AFTER
        )]
    )
    fun onInit(gameConfig: GameConfig, ci: CallbackInfo) {
    }

    @Inject(method = ["<init>"], at = [At(value = "RETURN")])
    fun onInit_Post(gameConfig: GameConfig, ci: CallbackInfo) {
        initialize()
        postInitialize()
    }

    @Inject(method = ["close"], at = [At("HEAD")])
    fun onClose(ci: CallbackInfo) {
        NullHackMod.LOGGER.warn("Shutting down " + NullHackMod.NAME)
        onExit()
        save()
    }
}