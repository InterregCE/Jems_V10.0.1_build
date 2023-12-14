package io.cloudflight.jems.server.common.minio

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.cloudflight.jems.server.common.exception.DuplicateFileException
import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.common.file.minio.MinioStorageImpl
import io.minio.BucketExistsArgs
import io.minio.GetObjectArgs
import io.minio.GetObjectResponse
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.RemoveObjectsArgs
import io.minio.Result
import io.minio.errors.ErrorResponseException
import io.minio.messages.Contents
import io.minio.messages.DeleteObject
import io.minio.messages.ErrorResponse
import io.minio.messages.Item
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class MinioStorageTest {

    private val zone = ZoneId.of("UTC")
    private val sourceBucketName = "source-bucket"
    private val sourceObjectName = "source-object"
    private val destinationBucketName = "destination-bucket"
    private val destinationObjectName = "destination-object"


    @MockK
    lateinit var minioClient: MinioClient

    @MockK
    lateinit var exception: ErrorResponseException

    lateinit var minioStorage: MinioStorage

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        minioStorage = MinioStorageImpl(minioClient)
    }

    @Test
    fun `should throw DuplicateFileException if it already exists and overwrite is not provided`() {
        every { minioClient.bucketExists(bucketExistsArgs("bucket")) } returns true

        val contents = mockk<Contents>()
        val fileMetadata = Result<Item>(contents)

        every { contents.lastModified() } returns ZonedDateTime.of(LocalDateTime.of(2020, 6, 15, 7, 30), zone)
        every { contents.objectName() } returns "file"
        every { minioClient.listObjects(any()) } returns mutableListOf(fileMetadata)

        val exception = assertThrows<DuplicateFileException> {
            minioStorage.saveFile("bucket", "file", 0, InputStream.nullInputStream())
        }

        val expectedModified = ZonedDateTime.of(LocalDateTime.of(2020, 6, 15, 7, 30), zone)
        with(exception.error) {
            assertEquals(expectedModified.withZoneSameInstant(zone), updated.withZoneSameInstant(zone))
            assertEquals("file", name)
            assertEquals(DuplicateFileException.Origin.FILE_STORAGE, origin)
        }
    }

    @Test
    fun `should overwrite file if it already exists and overwrite is set to true`() {
        val content ="test"
        val bucketName = "bucket-name"
        val filePath = "filePath"

        val streamToSave = BufferedInputStream(content.toByteArray().inputStream())
        val contents = mockk<Contents>()
        val fileMetadata = Result<Item>(contents)
        val putObjectArgsSlot = slot<PutObjectArgs>()

        every { minioClient.bucketExists(bucketExistsArgs(bucketName)) } returns true
        every { contents.lastModified() } returns ZonedDateTime.of(LocalDateTime.of(2020, 6, 15, 7, 30), zone)
        every { contents.objectName() } returns filePath
        every { minioClient.listObjects(any()) } returns mutableListOf(fileMetadata)
        every {
            minioClient.putObject(capture(putObjectArgsSlot))
        } answers { ObjectWriteResponse(null, bucketName, null, filePath, null, null) }

        minioStorage.saveFile(bucketName, filePath, bucketName.length.toLong(), streamToSave, true)

        assertThat(bucketName).isEqualTo(putObjectArgsSlot.captured.bucket())
        assertThat(filePath).isEqualTo(putObjectArgsSlot.captured.`object`())
        assertThat(streamToSave).isEqualTo(putObjectArgsSlot.captured.stream())
        assertThat(content.toByteArray().size).isEqualTo(putObjectArgsSlot.captured.stream().readAllBytes().size)
    }

    @Test
    fun getFile() {
        every {
            minioClient.getObject(
                GetObjectArgs.builder().bucket("bucket").`object`("path").build()
            )
        } returns GetObjectResponse(null, null, null, null, "file_content".byteInputStream())
        assertTrue("file_content".toByteArray().contentEquals(minioStorage.getFile("bucket", "path")))
    }

    @Test
    fun getFile_utf8() {
        val testString = "¥£€\$¢₡₢₣₤₥₦₧₨₩₪₫₭₮₯₹ ᚠᛇᚻ᛫ᛒᛦᚦ᛫ᚠᚱᚩᚠᚢᚱ᛫ᚠᛁᚱᚪ᛫ᚷᛖᚻᚹᛦᛚᚳᚢᛗ \uD83D\uDE0D"
        every {
            minioClient.getObject(
                GetObjectArgs.builder().bucket("bucket").`object`("path").build()
            )
        } returns GetObjectResponse(null, null, null, null, testString.byteInputStream())
        assertTrue(testString.toByteArray().contentEquals(minioStorage.getFile("bucket", "path")))
    }

    @Test
    fun deleteFile_notExisting() {
        val logger: Logger = LoggerFactory.getLogger(MinioStorageImpl::class.java) as Logger
        val listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()
        logger.addAppender(listAppender)

        every { minioClient.listObjects(any()) } returns listOf()
        every { minioClient.bucketExists(any()) } returns false

        minioStorage.deleteFile("bucket", "path")
        assertLinesMatch(
            listOf("Attempt to delete a file bucket/path from MinIO storage, that does not exist."),
            listAppender.list.map { it.formattedMessage })
    }

    @Test
    fun deleteFile_ok() {
        val contents = mockk<Contents>()
        val fileMetadata = Result<Item>(contents)

        every { contents.lastModified() } returns ZonedDateTime.of(LocalDateTime.of(2020, 6, 15, 7, 30), zone)
        every { contents.objectName() } returns "file"
        every { minioClient.listObjects(any()) } returns mutableListOf(fileMetadata)
        every { minioClient.bucketExists(any()) } returns true

        every {
            minioClient.removeObject(
                RemoveObjectArgs.builder().bucket("bucket").`object`("path").build()
            )
        } answers { }

        minioStorage.deleteFile("bucket", "path")
    }

    @Test
    fun `should move file to the destination bucket and then remove it from source bucket`() {
        val contents = mockk<Contents>()
        val fileMetadata = Result<Item>(contents)

        every { contents.lastModified() } returns ZonedDateTime.of(LocalDateTime.of(2020, 6, 15, 7, 30), zone)
        every { contents.objectName() } returns "file"
        every { minioClient.listObjects(any()) } returns mutableListOf(fileMetadata)

        every { minioClient.bucketExists(bucketExistsArgs(destinationBucketName)) } returns true
        every { minioClient.copyObject(any()) } returns ObjectWriteResponse(
            null,
            destinationBucketName,
            null,
            destinationObjectName,
            null,
            null
        )
        every {
            minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(sourceBucketName).`object`(sourceObjectName).build()
            )
        } returns Unit

        every { minioClient.bucketExists(bucketExistsArgs(sourceBucketName)) } returns true
        minioStorage.moveFile(sourceBucketName, sourceObjectName, destinationBucketName, destinationObjectName)

        verifyOrder {
            minioClient.bucketExists(bucketExistsArgs(destinationBucketName))
            minioClient.copyObject(any())
            minioClient.listObjects(any())
            minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(sourceBucketName).`object`(sourceObjectName).build()
            )
        }
    }

    @Test
    fun `should create destination bucket and then move file to the destination bucket and then remove it from source bucket when destination bucket does not exist`() {

        val contents = mockk<Contents>()
        val fileMetadata = Result<Item>(contents)

        every { contents.lastModified() } returns ZonedDateTime.of(LocalDateTime.of(2020, 6, 15, 7, 30), zone)
        every { contents.objectName() } returns "file"
        every { minioClient.listObjects(any()) } returns mutableListOf(fileMetadata)
        every { minioClient.bucketExists(bucketExistsArgs(destinationBucketName)) } returns false
        every { minioClient.bucketExists(bucketExistsArgs(sourceBucketName)) } returns true
        every { minioClient.makeBucket(MakeBucketArgs.builder().bucket(destinationBucketName).build()) } returns Unit
        every {
            minioClient.copyObject(any())
        } returns ObjectWriteResponse(null, destinationObjectName, null, destinationObjectName, null, null)
        every {
            minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(sourceBucketName).`object`(sourceObjectName).build()
            )
        } returns Unit

        minioStorage.moveFile(sourceBucketName, sourceObjectName, destinationBucketName, destinationObjectName)

        verifyOrder {
            minioClient.bucketExists(bucketExistsArgs(destinationBucketName))
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(destinationBucketName).build())
            minioClient.copyObject(any())
            minioClient.listObjects(any())
            minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(sourceBucketName).`object`(sourceObjectName).build()
            )
        }
    }

    @Test
    fun `get file - not found gets logged`() {
        val logger: Logger = LoggerFactory.getLogger(MinioStorageImpl::class.java) as Logger
        val listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()
        logger.addAppender(listAppender)

        val bucket = "bucket"
        val file = "path"
        val errResponse = ErrorResponse("NoSuchKey", "The specified key does not exist.", bucket, "", file, "", "")
        val exBucketNotFound = ErrorResponseException(errResponse, null, "")
        every {
            minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).`object`(file).build()
            )
        } throws exBucketNotFound

        assertThrows<ErrorResponseException> { (minioStorage.getFile(bucket, file)) }
        assertLinesMatch(
            listOf("Template '$file' not found in Minio bucket '$bucket'!"),
            listAppender.list.map { it.formattedMessage })
    }

    @Test
    fun `get file - bucket not found is logged`() {
        val logger: Logger = LoggerFactory.getLogger(MinioStorageImpl::class.java) as Logger
        val listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()
        logger.addAppender(listAppender)

        val bucket = "bucket"
        val file = "path"
        val errResponse = ErrorResponse("NoSuchBucket", "Bucket does not exist.", bucket, "", file, "", "")
        val exBucketNotFound = ErrorResponseException(errResponse, null, "")
        every {
            minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).`object`(file).build()
            )
        } throws exBucketNotFound

        assertThrows<ErrorResponseException> { (minioStorage.getFile(bucket, file)) }
        assertLinesMatch(
            listOf("Bucket '$bucket' not found in Minio!"),
            listAppender.list.map { it.formattedMessage })
    }

    private fun bucketExistsArgs(bucket: String) =
        BucketExistsArgs.builder().bucket(bucket).build()

    @Test
    fun deleteFiles() {
        val contents = mockk<Contents>()
        val fileMetadata = Result<Item>(contents)

        every { contents.lastModified() } returns ZonedDateTime.of(LocalDateTime.of(2021, 7, 15, 7, 30), zone)
        every { contents.objectName() } returns "file"
        every { minioClient.listObjects(any()) } returns mutableListOf(fileMetadata)
        every { minioClient.bucketExists(any()) } returns true

        val slot = slot<RemoveObjectsArgs>()
        every {minioClient.removeObjects(capture(slot)) } returns listOf()

        val filePaths = listOf("path/file2", "path/file2", "path/file3")
        minioStorage.deleteFiles("bucket", filePaths)
        verify(exactly = 1) {
            minioClient.removeObjects(slot.captured)
        }
    }

}
