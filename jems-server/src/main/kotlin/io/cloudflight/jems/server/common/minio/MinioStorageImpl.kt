package io.cloudflight.jems.server.common.minio

import io.cloudflight.jems.server.common.exception.DuplicateFileException
import io.minio.BucketExistsArgs
import io.minio.CopyObjectArgs
import io.minio.CopySource
import io.minio.GetObjectArgs
import io.minio.ListObjectsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.messages.Item
import java.io.InputStream
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MinioStorageImpl(
    private val minioClient: MinioClient
) : MinioStorage {

    companion object {
        private val logger = LoggerFactory.getLogger(MinioStorageImpl::class.java)
    }

    override fun saveFile(bucket: String, filePath: String, size: Long, stream: InputStream) {
        makeBucketIfNotExists(bucket)
        throwIfObjectAlreadyExists(bucket, filePath)

        val arguments = PutObjectArgs.builder().bucket(bucket).`object`(filePath).stream(stream, size, -1).build()
        minioClient.putObject(arguments)
    }

    override fun moveFile(
        sourceBucket: String, sourceFilePath: String, destinationBucket: String, destinationFilePath: String
    ) {
        makeBucketIfNotExists(destinationBucket)
        minioClient.copyObject(
            CopyObjectArgs.builder()
                .bucket(destinationBucket)
                .`object`(destinationFilePath)
                .source(
                    CopySource.builder()
                        .bucket(sourceBucket)
                        .`object`(sourceFilePath)
                        .build()
                )
                .build()
        )
        deleteFile(sourceBucket, sourceFilePath)
    }

    override fun getFile(bucket: String, filePath: String): ByteArray {
        return IOUtils.toByteArray(
            minioClient.getObject(GetObjectArgs.builder().bucket(bucket).`object`(filePath).build())
        )
    }

    override fun deleteFile(bucket: String, filePath: String) {
        exists(bucket, filePath).also { objectAlreadyExists ->
            if (objectAlreadyExists)
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).`object`(filePath).build())
            else
                logger.error("Attempt to delete a file $bucket/$filePath from MinIO storage, that does not exist.")
        }
    }

    override fun exists(bucket: String, filePath: String): Boolean =
        getObjectInfoOrNull(bucket, filePath) != null

    private fun makeBucketIfNotExists(bucket: String) {
        if (!bucketExists(bucket)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build())
        }
    }

    private fun throwIfObjectAlreadyExists(bucket: String, filePath: String) =
        getObjectInfoOrNull(bucket, filePath)?.let {
            throw DuplicateFileException(it)
        }

    private fun getObjectInfoOrNull(bucket: String, filePath: String): Item? {
        if (!bucketExists(bucket))
            return null

        return with(
            minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).prefix(filePath).build())
                .map { it.get() }) {
            this.firstOrNull()
        }
    }

    private fun bucketExists(bucket: String): Boolean =
        minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())

}
