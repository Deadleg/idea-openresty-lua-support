package com.github.deadleg.idea.openresty.lua;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class NgxLuaDocumentationProvider extends AbstractDocumentationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(NgxLuaDocumentationProvider.class);

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return null;
    }

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        try {
            if (element == null || !element.getText().startsWith("ngx")) {
                return null;
            }
            URI path = getClass().getResource("/docs/" + element.getText() + ".html").toURI();
            if (!Files.exists(Paths.get(path))) {
                return null;
            }
            List<String> lines = Files.readAllLines(Paths.get(path));
            return String.join("\n", lines);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        try {
            if (originalElement == null || !originalElement.getText().startsWith("ngx")) {
                return null;
            }

            String text = originalElement.getText();
            int openBrace = text.indexOf('(');
            if (openBrace > -1) {
                text = text.substring(0, openBrace);
            }

            URI path = getClass().getResource("/quickDocs/" + text + ".txt").toURI();
            if (!Files.exists(Paths.get(path))) {
                return null;
            }
            List<String> lines = Files.readAllLines(Paths.get(path));
            return String.join("\n", lines);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        return contextElement.getParent().getParent();
    }
}
