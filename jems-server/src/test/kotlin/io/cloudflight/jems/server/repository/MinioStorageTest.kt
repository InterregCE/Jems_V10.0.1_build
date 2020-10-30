package io.cloudflight.jems.server.repository

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.cloudflight.jems.server.common.exception.DuplicateFileException
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.common.minio.MinioStorageImpl
import io.minio.ErrorCode
import io.minio.MinioClient
import io.minio.ObjectStat
import io.minio.PutObjectOptions
import io.minio.errors.ErrorResponseException
import io.minio.messages.ErrorResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import okhttp3.Headers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class MinioStorageTest {

    private val zone = ZoneId.of("UTC")

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

    @ParameterizedTest
    @CsvSource(value = ["NO_SUCH_KEY:true", "NO_SUCH_KEY:false", "NO_SUCH_OBJECT:true", "NO_SUCH_OBJECT:false"], delimiter = ':')
    fun saveFile_error_isBucket(errorCode: ErrorCode, bucketExists: Boolean) {
        testSave(bucketExists, errorCode)
    }

    @Test
    fun saveFile_error() {
        every { minioClient.bucketExists(eq("bucket")) } returns true

        every { exception.errorResponse() } returns getErrorResponse(ErrorCode.INTERNAL_ERROR)
        every { minioClient.statObject(eq("bucket"), eq("file")) } throws exception

        assertThrows<ErrorResponseException> { minioStorage.saveFile("bucket", "file", 0, InputStream.nullInputStream()) }
    }

    @Test
    fun saveFile_duplicate() {
        every { minioClient.bucketExists(eq("bucket")) } returns true

        val fileMetadata = ObjectStat(
            "bucket",
            "file",
            Headers.of(
                "Content-Length", "0",
                "Last-Modified", "Mon, 15 Jun 2020 07:30:00 GMT"))
        every { minioClient.statObject(eq("bucket"), eq("file")) } returns fileMetadata

        val exception = assertThrows<DuplicateFileException> {
            minioStorage.saveFile("bucket", "file", 0, InputStream.nullInputStream()) }

        val expectedModified = ZonedDateTime.of(LocalDateTime.of(2020, 6, 15, 7, 30), zone)
        with(exception.error) {
            assertEquals(expectedModified.withZoneSameInstant(zone), updated.withZoneSameInstant(zone))
            assertEquals("file", name)
            assertEquals(DuplicateFileException.Origin.FILE_STORAGE, origin)
        }
    }

    private fun testSave(bucketExists: Boolean, errorCode: ErrorCode) {
        val streamToSave = "test".toByteArray().inputStream()
        every { minioClient.bucketExists(eq("test_bucket")) } returns bucketExists

        val bucketToCreate = slot<String>()
        every { minioClient.makeBucket(capture(bucketToCreate)) } answers { }

        every { exception.errorResponse() } returns getErrorResponse(errorCode)
        every { minioClient.statObject(eq("test_bucket"), eq("test_file")) } throws exception

        val bucketToBe = slot<String>()
        val filePath = slot<String>()
        val stream = slot<InputStream>()
        val options = slot<PutObjectOptions>()
        every { minioClient.putObject(capture(bucketToBe), capture(filePath), capture(stream), capture(options)) } answers { }

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
        every { minioClient.getObject(eq("bucket"), eq("path")) } returns "file_content".byteInputStream()
        assertTrue("file_content".toByteArray().contentEquals(minioStorage.getFile("bucket", "path")))
    }

    @Test
    fun getFile_utf8() {
        val testString = "¥£€\$¢₡₢₣₤₥₦₧₨₩₪₫₭₮₯₹ ᚠᛇᚻ᛫ᛒᛦᚦ᛫ᚠᚱᚩᚠᚢᚱ᛫ᚠᛁᚱᚪ᛫ᚷᛖᚻᚹᛦᛚᚳᚢᛗ \uD83D\uDE0D"
        every { minioClient.getObject(eq("bucket"), eq("path")) } returns testString.byteInputStream()
        assertTrue(testString.toByteArray().contentEquals(minioStorage.getFile("bucket", "path")))
    }

    @Test
    fun deleteFile_error() {
        every { exception.errorResponse() } returns getErrorResponse(ErrorCode.INTERNAL_ERROR)
        every { minioClient.statObject(eq("bucket"), eq("path")) } throws exception

        assertThrows<ErrorResponseException> { minioStorage.deleteFile("bucket", "path") }
    }

    @Test
    fun deleteFile_notExisting() {
        val logger: Logger = LoggerFactory.getLogger(MinioStorageImpl::class.java) as Logger
        val listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()
        logger.addAppender(listAppender)

        every { exception.errorResponse() } returns getErrorResponse(ErrorCode.NO_SUCH_OBJECT)
        every { minioClient.statObject(eq("bucket"), eq("path")) } throws exception

        minioStorage.deleteFile("bucket", "path")
        assertLinesMatch(
            listOf("Attempt to delete a file bucket/path from MinIO storage, that does not exist."),
            listAppender.list.map { it.formattedMessage })
    }

    @Test
    fun deleteFile_ok() {
        every { minioClient.statObject(eq("bucket"), eq("path")) } returns null

        val bucketToBe = slot<String>()
        val fileToBe = slot<String>()
        every { minioClient.removeObject(capture(bucketToBe), capture(fileToBe)) } answers { }

        minioStorage.deleteFile("bucket", "path")

        assertEquals("bucket", bucketToBe.captured)
        assertEquals("path", fileToBe.captured)
    }

    private fun getErrorResponse(errorCode: ErrorCode): ErrorResponse {
        return ErrorResponse(errorCode, null, null, null, null, null)
    }

}
