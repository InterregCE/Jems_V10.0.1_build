package io.cloudflight.ems.repository

import io.cloudflight.ems.exception.DuplicateFileException
import io.minio.ErrorCode
import io.minio.MinioClient
import io.minio.ObjectStat
import io.minio.PutObjectOptions
import io.minio.errors.ErrorResponseException
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class MinioStorageImpl(
    private val minioClient: MinioClient
): MinioStorage {

    override fun saveFile(bucket: String, filePath: String, size: Long, stream: InputStream) {
        validateBucket(minioClient, bucket)
        validateFileUnique(minioClient, bucket, filePath)

        val options = PutObjectOptions(size, -1)
        minioClient.putObject(bucket, filePath, stream, options)
    }

    /*
    override fun listFilesInDir(bucket: String, dir: String): Iterable<Result<Item>> {
        validateBucket(minioClient, bucket)
        return minioClient.listObjects(bucket, dir)
    }
    */

    override fun getFile(bucket: String, filePath: String): ByteArray {
        return IOUtils.toByteArray(
            minioClient.getObject(bucket, filePath))
    }

    private fun validateBucket(client: MinioClient, bucket: String) {
        if (!client.bucketExists(bucket)) {
            client.makeBucket(bucket)
        }
    }

    private fun validateFileUnique(client: MinioClient, bucket: String, filePath: String) {
        val fileMetadata: ObjectStat
        try {
            fileMetadata = client.statObject(bucket, filePath)
        } catch (e: ErrorResponseException) {
            val reason = e.errorResponse().errorCode()
            if (reason == ErrorCode.NO_SUCH_KEY || reason == ErrorCode.NO_SUCH_OBJECT) {
                // if cannot be find, then everything is alright
                return
            }
            throw e
        }
        throw DuplicateFileException(fileMetadata)
    }

}
