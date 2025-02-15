package love.xiguajerry.nullhack.mixins.gui.screen;

import love.xiguajerry.nullhack.language.Config;
import love.xiguajerry.nullhack.interfaces.ILanguage;
import love.xiguajerry.nullhack.interfaces.ITranslationStorage;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SessionSearchTrees.class, priority = 990)
abstract class MixinSearchManager {
    @Redirect(method = "lambda$getTooltipLines$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;getString()Ljava/lang/String;"))
    private static String addFallbackTranslationsToSearchTooltips(Component instance) {
        var original = instance.getString();
        if (Config.getInstance() == null) return original;
        if (!Config.getInstance().multilingualItemSearch) return original;

        var translationStorage = ((ILanguage) Language.getInstance()).languagereload_getTranslationStorage();
        if (translationStorage == null) return original;

        var stringBuilder = new StringBuilder(original);
        for (String fallbackCode : Config.getInstance().fallbacks) {
            ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(fallbackCode);
            stringBuilder.append('\n').append(instance.getString());
        }

        ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(null);
        return stringBuilder.toString();
    }
}
