package io.cloudflight.jems.server.repository

import java.io.InputStream

interface MinioStorage {

    fun saveFile(bucket: String, filePath: String, size: Long, stream: InputStream)

    fun getFile(bucket: String, filePath: String): ByteArray

    fun deleteFile(bucket: String, filePath: String)

}
