package com.github.deadleg.idea.openresty.lua

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.lang.Language
import com.intellij.patterns.PlatformPatterns

class NgxLuaCompletionContributor : CompletionContributor() {
    init {
        val maybeLua = Language.findLanguageByID("Lua") ?: throw RuntimeException("No lua plugin is installed!")

        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(maybeLua),
                NgxLuaCompletionProvider())
    }
}
