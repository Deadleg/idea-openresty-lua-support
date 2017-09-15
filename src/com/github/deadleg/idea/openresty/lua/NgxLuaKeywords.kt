package com.github.deadleg.idea.openresty.lua

import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

internal object NgxLuaKeywords {
    /**
     * Contains the key and a list of sub functions e.g. ngx.location = capture
     */
    private val keywords: Map<String, List<String>>
    private val args: Map<String, List<String>>
    /**
     * Set of all keywords e.g. ngx.timer.at, ngx.location.capture
     */
    private val fullKeywords: Set<String>

    init {
        val keywordsTemp = mutableMapOf<String, MutableList<String>>()
        val argsTemp = mutableMapOf<String, MutableList<String>>()
        val fullKeywordsTemp = mutableSetOf<String>()
        try {
            val uri = javaClass.classLoader.getResource("/ngxkeywords.txt")!!.toURI()
            FileSystems.newFileSystem(uri, emptyMap<String, Void>())
            Files.lines(Paths.get(uri)).forEach { line ->
                fullKeywordsTemp.add("ngx." + line.split(' ')[0])
                val content = line.split("\\{?,?\\s\\}?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val fields = content[0].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in fields.indices) {
                    if (i == 0) {
                        if (!keywordsTemp.containsKey("ngx")) {
                            keywordsTemp.put("ngx", ArrayList())
                        }
                        keywordsTemp["ngx"]!!.add(fields[i])
                    } else {
                        var key = "ngx."
                        for (j in 0 until i) {
                            key += fields[j] + "."
                        }
                        key = key.substring(0, key.length - 1)
                        if (!keywordsTemp.containsKey(key)) {
                            keywordsTemp.put(key, ArrayList())
                        }
                        keywordsTemp[key]!!.add(fields[i])
                    }
                }

                if (content.size > 1) {
                    argsTemp.put("ngx." + content[0], content.drop(1).toMutableList())
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to load keywords", e)
        } catch (e: URISyntaxException) {
            throw RuntimeException("Failed to load keywokds", e)
        }

        keywords = keywordsTemp
        args = argsTemp
        fullKeywords = fullKeywordsTemp
    }

    fun getKeywords(key: String): List<String>? {
        return keywords[key]
    }

    fun getArgs(keyword: String): List<String>? {
        return args[keyword]
    }

    fun isAKeyword(keyword: String): Boolean {
        return fullKeywords.contains(keyword)
    }
}
