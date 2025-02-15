package love.xiguajerry.nullhack.interfaces;

import love.xiguajerry.nullhack.language.LanguageEntry;
import love.xiguajerry.nullhack.language.LanguageListWidget;

public interface ILanguageOptionsScreen {
    void languagereload_focusList(LanguageListWidget list);

    void languagereload_focusEntry(LanguageEntry entry);
}
