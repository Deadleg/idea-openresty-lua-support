package com.github.deadleg.idea.openresty.lua

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

class NgxLuaCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        val originalPosition = parameters.originalPosition ?: return
        val element = originalPosition.parent

        if (element.javaClass.name.contains("LuaGlobalUsage")) {
            result.addElement(LookupElementBuilder.create("ngx"))
        } else if ((element.javaClass.name.contains("LuaCompoundIdentifier") || element.parent.javaClass.name.contains("LuaCompoundIdentifier")) && isNgxCall(element)) {
            // Check that the tree has LuaCompoundIdentifier as parent, with first
            // prev sibling is '.', and the prev sibling of that is 'ngx'.
            val key = constructNgxCall(element)

            val keywords = NgxLuaKeywords.getKeywords(key) ?: return
            keywords.forEach { keyword -> result.addElement(LookupElementBuilder.create(keyword)) }
        }
    }

    private fun constructNgxCall(element: PsiElement): String {
        // if '.' LuaCompoundReference -> LuaCompoundIdentifier
        // else LuaCompoundIdentifier -> LuaCompoundReference
        val siblings = element.parent.parent.children
        val text = siblings[0].text
        return text.substring(0, text.lastIndexOf('.'))
    }

    private fun isNgxCall(element: PsiElement): Boolean {
        val siblings = element.parent.parent.children
        if (siblings[0].text.startsWith("ngx")) {
            return true
        }
        return false
    }

    private fun getParentOfType(className: String, child: PsiElement): PsiElement? {
        var parent = child.parent
        for (i in 0..9) {
            if (parent.javaClass.name.contains(className)) {
                return parent
            } else {
                parent = parent.parent
            }
        }
        return null
    }
}
