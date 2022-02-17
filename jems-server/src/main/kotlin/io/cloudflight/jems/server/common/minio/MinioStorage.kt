package io.cloudflight.jems.server.common.minio

import java.io.InputStream

interface MinioStorage {

    fun saveFile(bucket: String, filePath: String, size: Long, stream: InputStream, overwriteIfExists: Boolean = false)

    fun moveFile(sourceBucket: String, sourceFilePath: String, destinationBucket: String, destinationFilePath: String)

    fun getFile(bucket: String, filePath: String): ByteArray

    fun deleteFile(bucket: String, filePath: String)

    fun exists(bucket: String, filePath: String): Boolean

}
