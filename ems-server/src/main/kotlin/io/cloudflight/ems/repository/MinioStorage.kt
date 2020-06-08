package io.cloudflight.ems.repository

import java.io.InputStream

interface MinioStorage {

    fun saveFile(bucket: String, filePath: String, size: Long, stream: InputStream)

    fun getFile(bucket: String, filePath: String): ByteArray

}
