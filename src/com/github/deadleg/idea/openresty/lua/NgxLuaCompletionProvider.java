package com.github.deadleg.idea.openresty.lua;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import javax.print.DocFlavor;

public class NgxLuaCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement originalPosition = parameters.getOriginalPosition();
        if (originalPosition == null) {
            return;
        }
        PsiElement element = originalPosition.getParent();
        PsiElement maybeExpressionList = getParentOfType("LuaExpressionList", element);
        if (maybeExpressionList != null) {
            // We are in a function list
        }

        if (element.getClass().getName().contains("LuaGlobalUsage")) {
            result.addElement(LookupElementBuilder.create("ngx"));
        } else if ((element.getClass().getName().contains("LuaCompoundIdentifier")
                || element.getParent().getClass().getName().contains("LuaCompoundIdentifier"))
                && isNgxCall(element)) {
            // Check that the tree has LuaCompoundIdentifier as parent, with first
            // prev sibling is '.', and the prev sibling of that is 'ngx'.
            String key = constructNgxCall(element);

            NgxLuaKeywords.getKeywords(key)
                    .forEach(keyword -> result.addElement(LookupElementBuilder.create(keyword)));
        }
    }

    private String constructNgxCall(PsiElement element) {
        PsiElement[] siblings = element.getParent().getParent().getChildren();
        String text = siblings[0].getText();
        return text.substring(0, text.lastIndexOf('.')) ;
    }

    private boolean isNgxCall(PsiElement element) {
        PsiElement[] siblings = element.getParent().getParent().getChildren();
        if (siblings[0].getText().startsWith("ngx")) {
            return true;
        }
        return false;
    }

    private PsiElement getParentOfType(String className, PsiElement child) {
        PsiElement parent = child.getParent();
        for (int i = 0; i < 10; i++) {
            if (parent.getClass().getName().contains(className)) {
                return parent;
            } else {
                parent = parent.getParent();
            }
        }
        return null;
    }
}
