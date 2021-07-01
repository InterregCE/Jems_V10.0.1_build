package io.cloudflight.jems.server.programme.controller.translation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.translation.TranslationFileTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.translation.download_translation_file.DownloadTranslationFileFailed
import io.cloudflight.jems.server.programme.service.translation.download_translation_file.DownloadTranslationFileInteractor
import io.cloudflight.jems.server.programme.service.translation.list_translation_files.ListTranslationFilesException
import io.cloudflight.jems.server.programme.service.translation.list_translation_files.ListTranslationFilesInteractor
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.upload_translation_file.UploadTranslationFileFailed
import io.cloudflight.jems.server.programme.service.translation.upload_translation_file.UploadTranslationFileInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import java.io.InputStream
import java.time.ZonedDateTime

internal class TranslationFileControllerTest : UnitTest() {

    private val fileSize = 50L
    private val fileType = TranslationFileTypeDTO.System
    private val language = SystemLanguage.EN
    private val fileByteArray = ByteArray(fileSize.toInt())
    private val fileMetaData = TranslationFileMetaData(language, fileType.toModel(), ZonedDateTime.now())
    val file = MockMultipartFile("name.txt", "name.txt", "text/csv", fileByteArray)

    @MockK
    lateinit var downloadTranslationFile: DownloadTranslationFileInteractor

    @MockK
    lateinit var uploadTranslationFile: UploadTranslationFileInteractor

    @MockK
    lateinit var listTranslationFiles: ListTranslationFilesInteractor

    @InjectMockKs
    lateinit var translationFileController: TranslationFileController

    @BeforeEach
    fun reset() {
        clearMocks(downloadTranslationFile)
        clearMocks(uploadTranslationFile)
        clearMocks(listTranslationFiles)
    }

    @Test
    fun `should return list of translation files meta data`() {

        every { listTranslationFiles.list() } returns listOf(fileMetaData)

        assertThat(translationFileController.get())
            .containsExactly(fileMetaData.toDTO())

        verify(exactly = 1) { listTranslationFiles.list() }

    }

    @Test
    fun `should upload translation file`() {
        val slot = slot<InputStream>()

        every {
            uploadTranslationFile.upload(
                fileType.toModel(), language, capture(slot), file.size
            )
        } returns fileMetaData

        assertThat(translationFileController.upload(fileType, language, file))
            .isEqualTo(fileMetaData.toDTO())

        verify(exactly = 1) { uploadTranslationFile.upload(fileType.toModel(), language, slot.captured, file.size) }

    }


    @Test
    fun `should return translation file ByteArray`() {

        every { downloadTranslationFile.download(fileType.toModel(), language) } returns fileByteArray

        assertThat(translationFileController.download(fileType, language))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(file.size)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"${fileType.toModel().getFileNameFor(language)}\""
                    ).body(ByteArrayResource(fileByteArray))
            )

        verify(exactly = 1) { downloadTranslationFile.download(fileType.toModel(), language) }

    }

    @Test
    fun `should throw DownloadTranslationFileFailed when there is problem in downloading the file`() {


        every { downloadTranslationFile.download(fileType.toModel(), language) } throws DownloadTranslationFileFailed(
            RuntimeException("download failed")
        )

        assertThrows<DownloadTranslationFileFailed> { (translationFileController.download(fileType, language)) }

        verify(exactly = 1) { downloadTranslationFile.download(fileType.toModel(), language) }

    }

    @Test
    fun `should throw UploadTranslationFileFailed when there is problem in downloading the file`() {

        val slot = slot<InputStream>()

        every {
            uploadTranslationFile.upload(
                fileType.toModel(), language, capture(slot), file.size
            )
        } throws UploadTranslationFileFailed(RuntimeException("upload failed"))

        assertThrows<UploadTranslationFileFailed> { (translationFileController.upload(fileType, language, file)) }

        verify(exactly = 1) { uploadTranslationFile.upload(fileType.toModel(), language, slot.captured, file.size) }

    }


    @Test
    fun `should throw ListTranslationFilesException when there is problem in fetching list of translation files meta data`() {

        every { listTranslationFiles.list() } throws ListTranslationFilesException(RuntimeException("listing translation files meta data failed"))

        assertThrows<ListTranslationFilesException> { (listTranslationFiles.list()) }

        verify(exactly = 1) { listTranslationFiles.list() }

    }
}
