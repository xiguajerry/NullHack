package love.xiguajerry.nullhack.mixins;

import love.xiguajerry.nullhack.NullHackMod;
import love.xiguajerry.nullhack.language.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(Options.class)
abstract class MixinOptions {
    @Shadow
    @Final
    private File optionsFile;
    @Shadow public String languageCode;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Minecraft client, File optionsFile, CallbackInfo ci) {
        if (!NullHackMod.INSTANCE.getShouldSetSystemLanguage()) {
            nullhack_nextgen$checkConfigLanguage(languageCode);
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    void onLoad(CallbackInfo ci) {
        if (!optionsFile.exists()) {
            NullHackMod.INSTANCE.setShouldSetSystemLanguage(true);
        }
    }

    @Inject(method = "dataFix", at = @At("RETURN"))
    void onUpdate(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        var lang = cir.getReturnValue().getString("lang");
        if (lang.isEmpty()) {
            NullHackMod.INSTANCE.setShouldSetSystemLanguage(true);
        } else nullhack_nextgen$checkConfigLanguage(lang);
    }

    @Unique
    private static void nullhack_nextgen$checkConfigLanguage(String language) {
        var config = Config.getInstance();
        if (!config.language.equals(language)) {
            NullHackMod.LOGGER.info(
                    "Game language ({}) and config language ({}) are different. Updating config",
                    language,
                    config.language
            );
            config.previousLanguage = config.language;
            config.previousFallbacks = config.fallbacks;
            config.language = language;
            config.fallbacks.clear();
            if (!language.equals(Language.DEFAULT))
                config.fallbacks.add(Language.DEFAULT);
            Config.save();
        }
    }
}