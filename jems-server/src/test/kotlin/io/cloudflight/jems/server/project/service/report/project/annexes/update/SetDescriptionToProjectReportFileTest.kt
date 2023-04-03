package io.cloudflight.jems.server.project.service.report.project.annexes.update

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetDescriptionToProjectReportFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L
        const val pathPrefix = "Project/000001/Report/ProjectReport/000002/"
    }

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var fileService: JemsProjectFileService

    @InjectMockKs
    lateinit var interactor: SetDescriptionToProjectReportFile

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, filePersistence, fileService)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
                AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun `should successfully set the description for a project report file (root)`() {
        val fileId = 3L
        every { filePersistence.existsReportFile(PROJECT_ID, pathPrefix, fileId) } returns true
        every { fileService.setDescription(fileId, "updated desc") } answers { }

        interactor.update(PROJECT_ID, REPORT_ID, fileId, "updated desc")
        verify(exactly = 1) { fileService.setDescription(fileId, "updated desc") }
    }

    @Test
    fun `should throw FileNotFound when said file does not exist`() {
        val fileId = 4L
        every { filePersistence.existsReportFile(PROJECT_ID, pathPrefix, fileId) } returns false
        assertThrows<FileNotFound> { interactor.update(PROJECT_ID, REPORT_ID, fileId, "test") }
    }
}
