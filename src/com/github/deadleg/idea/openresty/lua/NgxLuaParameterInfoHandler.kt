package com.github.deadleg.idea.openresty.lua

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.lang.parameterInfo.ParameterInfoContext
import com.intellij.lang.parameterInfo.ParameterInfoHandler
import com.intellij.lang.parameterInfo.ParameterInfoUIContext
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext
import com.intellij.psi.PsiElement

class NgxLuaParameterInfoHandler : ParameterInfoHandler<PsiElement, Any> {
    override fun couldShowInLookup(): Boolean {
        return true
    }

    override fun getParametersForLookup(item: LookupElement, context: ParameterInfoContext): Array<Any>? {
        return null
    }

    override fun getParametersForDocumentation(p: Any, context: ParameterInfoContext): Array<Any>? {
        return null
    }

    override fun findElementForParameterInfo(context: CreateParameterInfoContext): PsiElement? {
        val offset = context.offset
        // LuaFunctionCallExpression
        return context.file.findElementAt(offset)?.parent?.parent
    }

    override fun showParameterInfo(element: PsiElement, context: CreateParameterInfoContext) {
        // The lua plugin PSI elements have lua in its name
        if (!element.javaClass.name.contains("Lua")) {
            return
        }
        // Remove braces
        val functionElement = if ("com.sylvanaar.idea.Lua.lang.psi.impl.lists.LuaExpressionListImpl" == element.javaClass.name) {
            element.parent.parent
        } else {
            element
        }
        val function = if (functionElement.textContains('(')) {
            functionElement.text.substring(0, functionElement.text.indexOf('('))
        } else {
            functionElement.text
        }
        val parameters = NgxLuaKeywords.getArgs(function) ?: return

        context.itemsToShow = parameters.toTypedArray()
        context.showHint(element, element.textRange.startOffset, this)
    }

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): PsiElement? {
        return context.file.findElementAt(context.offset)?.parent?.parent
    }

    override fun updateParameterInfo(psiElement: PsiElement, context: UpdateParameterInfoContext) {
        // The lua plugin PSI elements have lua in its name
        if (!psiElement.javaClass.name.contains("Lua")) {
            return
        }
        // Remove braces
        val function = if (psiElement.textContains('(')) {
            psiElement.text.substring(0, psiElement.text.indexOf('('))
        } else {
            // Case when getting info from just ngx.location.capture, not ngx.location.capture()
            psiElement.text
        }
        val parameters = NgxLuaKeywords.getArgs(function) ?: return

        // Traverse up the tree until we hit the parent that holds the children
        var root = context.file.findElementAt(context.offset - 1) // Get element at start of cursor
        for (i in 0..2) {
            root = root?.parent
            if (root == null) {
                return
            }
        }

        var numberOfArgs = root!!.children.size
        if (numberOfArgs < 0) {
            numberOfArgs = 0
        } else if (numberOfArgs > parameters.size) {
            return
        }
        context.highlightedParameter = parameters[numberOfArgs - 1]
    }

    override fun getParameterCloseChars(): String? {
        return ",){}\t"
    }

    override fun tracksParameterIndex(): Boolean {
        return true
    }

    override fun updateUI(p: Any, context: ParameterInfoUIContext) {
        val highlightStartOffset = -1
        val highlightEndOffset = -1

        val buffer = StringBuilder()

        if (p is PsiElement) {
            buffer.append(p.text)
        }

        if (p is String) {
            buffer.append(p)
        }

        context.setupUIComponentPresentation(
                buffer.toString(),
                highlightStartOffset,
                highlightEndOffset,
                !context.isUIComponentEnabled,
                false,
                false,
                context.defaultParameterColor)
    }
}
