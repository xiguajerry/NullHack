package love.xiguajerry.nullhack.mixins.gui.screen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import love.xiguajerry.nullhack.language.Config;
import love.xiguajerry.nullhack.interfaces.ILanguage;
import love.xiguajerry.nullhack.interfaces.ITranslationStorage;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SessionSearchTrees.class, priority = 990)
abstract class MixinSearchManager {
    @ModifyExpressionValue(method = "lambda$getTooltipLines$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;getString()Ljava/lang/String;"))
    private static String addFallbackTranslationsToSearchTooltips(String original, Component tooltip) {
        if (Config.getInstance() == null) return original;
        if (!Config.getInstance().multilingualItemSearch) return original;

        var translationStorage = ((ILanguage) Language.getInstance()).languagereload_getTranslationStorage();
        if (translationStorage == null) return original;

        var stringBuilder = new StringBuilder(original);
        for (String fallbackCode : Config.getInstance().fallbacks) {
            ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(fallbackCode);
            stringBuilder.append('\n').append(tooltip.getString());
        }

        ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(null);
        return stringBuilder.toString();
    }
}
