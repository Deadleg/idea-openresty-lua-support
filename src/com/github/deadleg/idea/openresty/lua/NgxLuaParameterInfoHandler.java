package com.github.deadleg.idea.openresty.lua;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoHandler;
import com.intellij.lang.parameterInfo.ParameterInfoUIContext;
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.java.PsiEmptyExpressionImpl;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NgxLuaParameterInfoHandler implements ParameterInfoHandler<PsiElement, Object> {
    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Nullable
    @Override
    public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
        return new Object[0];
    }

    @Nullable
    @Override
    public Object[] getParametersForDocumentation(Object p, ParameterInfoContext context) {
        return new Object[0];
    }

    @Nullable
    @Override
    public PsiElement findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        int offset = context.getOffset();
        // LuaFunctionCallExpression
        return context.getFile().findElementAt(offset).getParent().getParent();
    }

    @Override
    public void showParameterInfo(@NotNull PsiElement element, @NotNull CreateParameterInfoContext context) {
        // The lua plugin PSI elements have lua in its name
        if (!element.getClass().getName().contains("Lua")) {
            return;
        }
        // Remove braces
        String function = element.getText().substring(0, element.getTextLength() - 2);
        String[] parameters = NgxLuaKeywords.getArgs(function);
        if (parameters == null) {
            return;
        }

        context.setItemsToShow(parameters);
        context.showHint(element, element.getTextRange().getStartOffset(), this);
    }

    @Nullable
    @Override
    public PsiElement findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        return context.getFile().findElementAt(context.getOffset()).getParent().getParent();
    }

    @Override
    public void updateParameterInfo(@NotNull PsiElement psiElement, @NotNull UpdateParameterInfoContext context) {
        // The lua plugin PSI elements have lua in its name
        if (!psiElement.getClass().getName().contains("Lua")) {
            return;
        }
        // Remove braces
        String function = psiElement.getText().substring(0, psiElement.getText().indexOf('('));
        String[] parameters = NgxLuaKeywords.getArgs(function);
        if (parameters == null) {
            return;
        }

        // Traverse up the tree until we hit the parent that holds the children
        PsiElement root = context.getFile().findElementAt(context.getOffset() - 1); // Get element at start of cursor
        for (int i = 0; i < 3; i++) {
            root = root.getParent();
            if (root == null) {
                return;
            }
        }

        int numberOfArgs = root.getChildren().length;
        if (numberOfArgs < 0) {
            numberOfArgs = 0;
        } else if (numberOfArgs > parameters.length) {
            return;
        }
        context.setHighlightedParameter(parameters[numberOfArgs - 1]);
    }

    @Nullable
    @Override
    public String getParameterCloseChars() {
        return ",){}\t";
    }

    @Override
    public boolean tracksParameterIndex() {
        return true;
    }

    @Override
    public void updateUI(Object p, @NotNull ParameterInfoUIContext context) {
        int highlightStartOffset = -1;
        int highlightEndOffset = -1;

        StringBuilder buffer = new StringBuilder();

        if (p instanceof PsiElement) {
            buffer.append(((PsiElement)p).getText());
        }

        if (p instanceof String) {
            buffer.append(p);
        }

        context.setupUIComponentPresentation(
                buffer.toString(),
                highlightStartOffset,
                highlightEndOffset,
                !context.isUIComponentEnabled(),
                false,
                false,
                context.getDefaultParameterColor()
        );
    }
}
