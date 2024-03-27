package io.cloudflight.jems.server.call.service.translation.deleteTranslationFile

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.config.AppProperties
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
import org.springframework.context.support.ReloadableResourceBundleMessageSource

class DeleteCallTranslationFileTest : UnitTest() {

    @MockK private lateinit var filePersistence: JemsFilePersistence
    @MockK private lateinit var fileService: JemsSystemFileService
    @MockK private lateinit var appProperties: AppProperties
    @MockK private lateinit var messageSource: ReloadableResourceBundleMessageSource

    @InjectMockKs private lateinit var interactor: DeleteCallTranslationFile

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence, fileService, appProperties, messageSource)
    }

    @Test
    fun `delete - not existing`() {
        every { filePersistence.fileIdIfExists("CallTranslation/000045/", "call-id-45-Application_no.properties") } returns null
        assertThrows<FileNotFound> { interactor.delete(45L, SystemLanguage.NO) }
        verify(exactly = 0) { fileService.archiveCallTranslation(any(), any(), any()) }
    }

    @Test
    fun `previous file is archived`() {
        every { filePersistence.fileIdIfExists("CallTranslation/000017/", "call-id-17-Application_no.properties") } returns 655L
        every{ messageSource.clearCache() } answers {}

        val fileArchivedNameSlot = slot<String>()
        every { fileService.archiveCallTranslation(655L, capture(fileArchivedNameSlot), "CallTranslationArchive/000017/") } answers { }
        every { appProperties.translationsFolder } returns "transl-folder"

        interactor.delete(17L, SystemLanguage.NO)
        verify(exactly = 1) { fileService.archiveCallTranslation(655L, any(), "CallTranslationArchive/000017/") }

        assertThat(fileArchivedNameSlot.captured).startsWith("archived-").endsWith("-call-id-17-Application_no.properties")
    }

}
