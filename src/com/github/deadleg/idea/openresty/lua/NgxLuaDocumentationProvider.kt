package com.github.deadleg.idea.openresty.lua

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths

class NgxLuaDocumentationProvider : AbstractDocumentationProvider() {

    override fun getUrlFor(element: PsiElement?, originalElement: PsiElement?): List<String>? {
        return null
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        try {
            if (element == null || element.text == null) {
                return null
            }

            if (!element.text.startsWith("ngx")) {
                return null
            }

            val path = javaClass.getResource("/docs/" + element.text + ".html").toURI()
            if (!Files.exists(Paths.get(path))) {
                return null
            }
            val lines = Files.readAllLines(Paths.get(path))
            return lines.joinToString("\n")
        } catch (e: IOException) {
            LOG.error("IOException getting log information for element {} and originalElement {}", e, element, originalElement)
        } catch (e: URISyntaxException) {
            LOG.error("URISyntaxException getting log information for element {} and originalElement {}", e, element, originalElement)
        }

        return null
    }

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        try {
            if (originalElement == null || !originalElement.text.startsWith("ngx")) {
                return null
            }

            var text = originalElement.text
            val openBrace = text.indexOf('(')
            if (openBrace > -1) {
                text = text.substring(0, openBrace)
            }

            val path = javaClass.getResource("/quickDocs/$text.txt").toURI()
            if (!Files.exists(Paths.get(path))) {
                return null
            }
            val lines = Files.readAllLines(Paths.get(path))
            return lines.joinToString("\n")
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        return null
    }

    override fun getCustomDocumentationElement(editor: Editor, file: PsiFile, contextElement: PsiElement?): PsiElement? {
        return contextElement!!.parent.parent
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NgxLuaDocumentationProvider::class.java)
    }
}
