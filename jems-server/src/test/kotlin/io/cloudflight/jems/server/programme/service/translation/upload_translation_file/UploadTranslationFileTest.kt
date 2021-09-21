package io.cloudflight.jems.server.programme.service.translation.upload_translation_file

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.config.AppResourcesProperties
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
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.ZonedDateTime
import org.apache.commons.io.FileUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.support.ReloadableResourceBundleMessageSource

internal class UploadTranslationFileTest : UnitTest() {

    private val fileSize = 50L
    private val fileType = TranslationFileType.System
    private val language = SystemLanguage.EN
    private val fileInputStream = ByteArrayInputStream(ByteArray(fileSize.toInt()))
    private val fileMetaData = TranslationFileMetaData(language, fileType, ZonedDateTime.now())
    private val translationsFolder = "src/test/resources/translations"
    private val expectedAuditCandidate = AuditCandidate(
        action = AuditAction.PROGRAMME_TRANSLATION_FILE_UPLOADED,
        description = "Translation file ".plus(fileType).plus("_").plus(language.name.toLowerCase()).plus(".properties uploaded"),
    )

    @MockK
    lateinit var translationFilePersistence: TranslationFilePersistence

    @MockK
    lateinit var appResourcesProperties: AppResourcesProperties

    @RelaxedMockK
    lateinit var messageSource: ReloadableResourceBundleMessageSource

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

        val slotAudit = slot<AuditCandidateEvent>()
        val fileByteArraySlot = slot<ByteArrayInputStream>()

        every { translationFilePersistence.exists(fileType, language) } returns false
        every { auditPublisher.publishEvent(capture(slotAudit)) } answers {}
        every {
            translationFilePersistence.save(fileType, language, capture(fileByteArraySlot), fileSize)
        } returns fileMetaData
        every { appResourcesProperties.translationsFolder } returns translationsFolder

        assertThat(uploadTranslationFile.upload(fileType, language, fileInputStream, fileSize))
            .isEqualTo(fileMetaData)

        assertThat(Files.exists(Path.of(translationsFolder, fileType.getFileNameFor(language)))).isEqualTo(true)
        FileUtils.deleteDirectory(File(translationsFolder))

        assertThat(slotAudit.captured.auditCandidate).isEqualTo(expectedAuditCandidate)

        verify(exactly = 1) { translationFilePersistence.exists(fileType, language) }
        verify(exactly = 1) { translationFilePersistence.save(fileType, language, fileByteArraySlot.captured, fileSize) }
        verify(exactly = 1) { auditPublisher.publishEvent(slotAudit.captured) }

    }

    @Test
    fun `should archive existing translation file and then upload new translation file and return new translation file meta data when translation file already exist`() {

        val fileByteArraySlot = slot<ByteArrayInputStream>()

        every { translationFilePersistence.exists(fileType, language) } returns true
        every { translationFilePersistence.archiveTranslationFile(fileType, language) } returns Unit
        every {
            translationFilePersistence.save(fileType, language, capture(fileByteArraySlot), fileSize)
        } returns fileMetaData
        every { appResourcesProperties.translationsFolder } returns translationsFolder

        assertThat(uploadTranslationFile.upload(fileType, language, fileInputStream, fileSize))
            .isEqualTo(fileMetaData)

        assertThat(Files.exists(Path.of(translationsFolder, fileType.getFileNameFor(language)))).isEqualTo(true)
        FileUtils.deleteDirectory(File(translationsFolder))

        verify(exactly = 1) { translationFilePersistence.save(fileType, language, fileByteArraySlot.captured, fileSize) }
        verify(exactly = 1) { translationFilePersistence.exists(fileType, language) }
        verify(exactly = 1) { translationFilePersistence.archiveTranslationFile(fileType, language) }

    }
}
