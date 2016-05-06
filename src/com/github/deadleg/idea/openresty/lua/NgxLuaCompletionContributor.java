package com.github.deadleg.idea.openresty.lua;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.Language;
import com.intellij.patterns.PlatformPatterns;

public class NgxLuaCompletionContributor extends CompletionContributor {
    public NgxLuaCompletionContributor() {
        Language maybeLua = Language.findLanguageByID("Lua");
        if (maybeLua == null) {
            throw new RuntimeException("No lua plugin is installed!");
        }

        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .withLanguage(maybeLua),
                new NgxLuaCompletionProvider()
        );
    }
}
