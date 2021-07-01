package io.cloudflight.jems.server.programme.service.translation.upload_translation_file

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.io.ByteArrayInputStream
import java.time.ZonedDateTime

internal class UploadTranslationFileTest : UnitTest() {

    private val fileSize = 50L
    private val fileType = TranslationFileType.System
    private val language = SystemLanguage.EN
    private val fileInputStream = ByteArrayInputStream(ByteArray(fileSize.toInt()))
    private val fileMetaData = TranslationFileMetaData(language, fileType, ZonedDateTime.now())

    @MockK
    lateinit var translationFilePersistence: TranslationFilePersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var uploadTranslationFile: UploadTranslationFile

    @BeforeEach
    fun reset() {
        clearMocks(auditPublisher)
        clearMocks(translationFilePersistence)
    }

    @Test
    fun `should upload translation file and return translation file meta data when translation file does not already exist`() {

        val slot = slot<UploadTranslationFileEvent>()

        every { translationFilePersistence.exists(fileType, language) } returns false
        every { auditPublisher.publishEvent(capture(slot)) } returns Unit
        every {
            translationFilePersistence.save(fileType, language, fileInputStream, fileSize)
        } returns fileMetaData

        assertThat(uploadTranslationFile.upload(fileType, language, fileInputStream, fileSize))
            .isEqualTo(fileMetaData)

        assertThat(slot.captured.translationFileMetaData).isEqualTo(fileMetaData)
        assertThat(slot.captured.translationFile).isEqualTo(fileInputStream)
        assertThat(slot.captured.context).isEqualTo(uploadTranslationFile)

        verify(exactly = 1) { translationFilePersistence.exists(fileType, language) }
        verify(exactly = 1) { translationFilePersistence.save(fileType, language, fileInputStream, fileSize) }
        verify(exactly = 1) { auditPublisher.publishEvent(slot.captured) }

    }

    @Test
    fun `should archive existing translation file and then upload new translation file and return new translation file meta data when translation file already exist`() {

        every { translationFilePersistence.exists(fileType, language) } returns true
        every { translationFilePersistence.archiveTranslationFile(fileType, language) } returns Unit
        every {
            translationFilePersistence.save(fileType, language, fileInputStream, fileSize)
        } returns fileMetaData

        assertThat(uploadTranslationFile.upload(fileType, language, fileInputStream, fileSize))
            .isEqualTo(fileMetaData)

        verify(exactly = 1) { translationFilePersistence.save(fileType, language, fileInputStream, fileSize) }
        verify(exactly = 1) { translationFilePersistence.exists(fileType, language) }
        verify(exactly = 1) { translationFilePersistence.archiveTranslationFile(fileType, language) }

    }
}
