package io.cloudflight.jems.server.call.service.translation.uploadTranslationFile

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.service.model.translation.CallTranslationFile
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.io.InputStream

class UploadCallTranslationFileTest : UnitTest() {

    companion object {
        const val translationCallSpecific = "call-id-7-Application_no.properties"
        const val translationProcessed = "Application_no.properties"
        const val translationWithIssues = "example-with-wrong-lines.properties"
    }

    @MockK private lateinit var filePersistence: JemsFilePersistence
    @MockK private lateinit var fileService: JemsSystemFileService
    @MockK private lateinit var securityService: SecurityService
    @MockK private lateinit var appProperties: AppProperties
    @MockK private lateinit var messageSource: ReloadableResourceBundleMessageSource

    @InjectMockKs private lateinit var interactor: UploadCallTranslationFile

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence, fileService, securityService, appProperties, messageSource)
    }

    @Test
    fun `upload - correct file`() {
        every { filePersistence.fileIdIfExists("CallTranslation/000007/", translationCallSpecific) } returns null

        uploadTest("call-specific-translation/$translationProcessed")
        verify(exactly = 0) { fileService.moveFile(any(), any(), any()) }
    }

    @Test
    fun `upload - file with errors`() {
        every { filePersistence.fileIdIfExists("CallTranslation/000007/", translationCallSpecific) } returns null

        uploadTest("call-specific-translation/$translationWithIssues")
        verify(exactly = 0) { fileService.moveFile(any(), any(), any()) }
    }

    @Test
    fun `previous file is archived`() {
        every { filePersistence.fileIdIfExists("CallTranslation/000007/", translationCallSpecific) } returns 497L
        val fileArchivedNameSlot = slot<String>()
        every { fileService.moveFile(497L, capture(fileArchivedNameSlot), "CallTranslationArchive/000007/") } answers { }

        uploadTest("call-specific-translation/$translationProcessed")
        verify(exactly = 1) { fileService.moveFile(497L, any(), "CallTranslationArchive/000007/") }

        assertThat(fileArchivedNameSlot.captured).startsWith("archived-").endsWith("-call-id-7-Application_no.properties")
    }

    private fun uploadTest(filePath: String) {
        val toSave = slot<JemsFileCreate>()
        val mockResult = mockk<JemsFileMetadata>()
        every { securityService.getUserIdOrThrow() } returns 7965L
        every { fileService.persistFile(capture(toSave)) } returns mockResult

        every { appProperties.translationsFolder } returns "transl-folder"
        every { messageSource.clearCache() } answers { }

        val file = mockk<ProjectFile>()
        every { file.stream } returns javaClass.classLoader.getResourceAsStream(filePath)!!

        assertThat(interactor.upload(7L, SystemLanguage.NO, file)).isEqualTo(
            CallTranslationFile(SystemLanguage.NO, mockResult, null)
        )
        verify(exactly = 1) { messageSource.clearCache() }

        val emptyStream = InputStream.nullInputStream()
        assertThat(toSave.captured.copy(content = emptyStream)).isEqualTo(
            JemsFileCreate(
                projectId = null,
                partnerId = null,
                name = translationCallSpecific,
                path = "CallTranslation/000007/",
                type = JemsFileType.CallTranslation,
                size = 1304,
                content = emptyStream,
                userId = 7965L,
                defaultDescription = "NO",
            )
        )
        assertThat(toSave.captured.content.readAllBytes()).isEqualTo(
            javaClass.classLoader.getResourceAsStream("call-specific-translation/$translationCallSpecific")!!.readAllBytes()
                .filter { it != '\r'.code.toByte() }.toByteArray()
        )
    }

}
