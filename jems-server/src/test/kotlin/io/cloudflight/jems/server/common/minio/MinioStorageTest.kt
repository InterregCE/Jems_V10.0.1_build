package io.cloudflight.jems.server.common.minio

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.cloudflight.jems.server.common.exception.DuplicateFileException
import io.minio.BucketExistsArgs
import io.minio.GetObjectArgs
import io.minio.GetObjectResponse
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.Result
import io.minio.StatObjectArgs
import io.minio.errors.ErrorResponseException
import io.minio.messages.Contents
import io.minio.messages.ErrorResponse
import io.minio.messages.Item
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifyOrder
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory

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

//    @ParameterizedTest
//    @CsvSource(
//        value = ["NO_SUCH_KEY:true", "NO_SUCH_KEY:false", "NO_SUCH_OBJECT:true", "NO_SUCH_OBJECT:false"],
//        delimiter = ':'
//    )
//    fun saveFile_error_isBucket(errorCode: ErrorResponse, bucketExists: Boolean) {
//        testSave(bucketExists, errorCode)
//    }

    @Test
    fun saveFile_duplicate() {
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

    private fun testSave(bucketExists: Boolean, errorCode: ErrorResponse) {
        val streamToSave = "test".toByteArray().inputStream()
        every { minioClient.bucketExists(bucketExistsArgs("test_bucket")) } returns bucketExists

        val bucketToCreate = slot<String>()
        every { minioClient.makeBucket(MakeBucketArgs.builder().bucket(capture(bucketToCreate)).build()) } answers { }

        every { minioClient.statObject(getStatObjectArgs("test_bucket", "test_file")) } throws exception

        val bucketToBe = slot<String>()
        val filePath = slot<String>()
        val stream = slot<InputStream>()
        val options = slot<PutObjectArgs>()
        every {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(capture(bucketToBe))
                    .`object`(capture(filePath))
                    .stream(capture(stream), "test".length.toLong(), -1)
                    .build()
            )
        } answers { ObjectWriteResponse(null, bucketToBe.toString(), null, filePath.toString(), null, null) }

        minioStorage.saveFile("test_bucket", "test_file", "test".length.toLong(), streamToSave)

        if (bucketExists) {
            assertFalse(bucketToCreate.isCaptured)
        } else {
            assertEquals("test_bucket", bucketToCreate.captured)
        }
        assertEquals("test_bucket", bucketToBe.captured)
        assertEquals("test_file", filePath.captured)
        assertEquals(streamToSave, stream.captured)
        assertEquals("test".length.toLong(), options.captured.objectSize())
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

    private fun getStatObjectArgs(bucket: String, filePath: String) =
        StatObjectArgs.builder().bucket(bucket).`object`(filePath).build()

    private fun bucketExistsArgs(bucket: String) =
        BucketExistsArgs.builder().bucket(bucket).build()

}
