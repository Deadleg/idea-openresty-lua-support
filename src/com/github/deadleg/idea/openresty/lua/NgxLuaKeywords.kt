package com.github.deadleg.idea.openresty.lua

import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

internal object NgxLuaKeywords {
    private val keywords: Map<String, List<String>>
    private val args: Map<String, List<String>>

    init {
        val keywordsTemp = mutableMapOf<String, MutableList<String>>()
        val argsTemp = mutableMapOf<String, MutableList<String>>()
        try {
            Files.lines(Paths.get(NgxLuaKeywords::class.java.classLoader.getResource("/ngxkeywords.txt")!!.toURI())).forEach { line ->
                val content = line.split("\\{?,?\\s\\}?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val fields = content[0].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in fields.indices) {
                    if (i == 0) {
                        if (!keywordsTemp.containsKey("ngx")) {
                            keywordsTemp.put("ngx", ArrayList<String>())
                        }
                        keywordsTemp["ngx"]!!.add(fields[i])
                    } else {
                        var key = "ngx."
                        for (j in 0..i - 1) {
                            key += fields[j] + "."
                        }
                        key = key.substring(0, key.length - 1)
                        if (!keywordsTemp.containsKey(key)) {
                            keywordsTemp.put(key, ArrayList<String>())
                        }
                        keywordsTemp[key]!!.add(fields[i])
                    }
                }

                if (content.size > 1) {
                    argsTemp.put("ngx." + content[0], content.drop(1).toMutableList())
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to load keyworkds", e)
        } catch (e: URISyntaxException) {
            throw RuntimeException("Failed to load keyworkds", e)
        }

        keywords = keywordsTemp
        args = argsTemp
    }

    fun getKeywords(key: String): List<String>? {
        return keywords[key]
    }

    fun getArgs(keyword: String): List<String>? {
        return args[keyword]
    }
}
