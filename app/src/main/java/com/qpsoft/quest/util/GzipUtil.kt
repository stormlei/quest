package com.qpsoft.quest.util

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPOutputStream

object GzipUtil {
    fun gzip(content: String): ByteArray {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).bufferedWriter(StandardCharsets.UTF_8).use { it.write(content) }
        return bos.toByteArray()
    }
}

