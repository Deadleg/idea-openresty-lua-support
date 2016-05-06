package com.github.deadleg.idea.openresty.lua;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class NgxLuaKeywords {
    private final static Map<String, List<String>> keywords;
    private final static Map<String, String[]> args;

    static {
        Map<String, List<String>> keywordsTemp = new HashMap<>();
        Map<String, String[]> argsTemp = new HashMap<>();
        try {
            Files.lines(Paths.get(NgxLuaKeywords.class.getClassLoader().getResource("/ngxkeywords.txt").toURI()))
                    .forEach(line -> {
                        String[] content = line.split(" ");
                        String[] fields = content[0].split("\\.");
                        for (int i = 0; i < fields.length; i++) {
                            if (i == 0) {
                                if (!keywordsTemp.containsKey("ngx")) {
                                    keywordsTemp.put("ngx", new ArrayList<>());
                                }
                                keywordsTemp.get("ngx").add(fields[i]);
                            } else {
                                String key = "ngx.";
                                for (int j = 0; j < i; j++) {
                                    key += fields[j] + ".";
                                }
                                key = key.substring(0, key.length() - 1);
                                if (!keywordsTemp.containsKey(key)) {
                                    keywordsTemp.put(key, new ArrayList<>());
                                }
                                keywordsTemp.get(key).add(fields[i]);
                            }
                        }

                        if (content.length > 1) {
                            argsTemp.put(content[0], content[1].split(", "));
                        }
                    });
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to load keyworkds", e);
        }

        keywords = Collections.unmodifiableMap(keywordsTemp);
        args = Collections.unmodifiableMap(argsTemp);
    }

    public static List<String> getKeywords(String key) {
        return keywords.get(key);
    }

    @Nullable
    public static String[] getArgs(String keyword) {
        return args.get(keyword);
    }
}
