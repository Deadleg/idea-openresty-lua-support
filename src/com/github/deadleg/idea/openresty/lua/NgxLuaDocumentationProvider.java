package com.github.deadleg.idea.openresty.lua;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class NgxLuaDocumentationProvider extends AbstractDocumentationProvider {
    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return Arrays.asList(element.getText());
    }

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        try {
            if (!element.getText().startsWith("ngx")) {
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
            if (!element.getText().startsWith("ngx")) {
                return null;
            }
            URI path = getClass().getResource("/quickDocs/" + element.getText() + ".html").toURI();
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
