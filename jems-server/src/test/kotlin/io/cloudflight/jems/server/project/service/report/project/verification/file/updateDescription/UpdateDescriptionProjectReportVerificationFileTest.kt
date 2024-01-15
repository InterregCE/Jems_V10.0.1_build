package io.cloudflight.jems.server.project.service.report.project.verification.file.updateDescription

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class UpdateDescriptionProjectReportVerificationFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 85L
        const val REPORT_ID = 87L
        const val FILE_ID = 89L
        fun filePath() = VerificationDocument.generatePath(PROJECT_ID, REPORT_ID)
    }

    @MockK
    lateinit var fileService: JemsProjectFileService

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var generalValidator: GeneralValidatorDefaultImpl

    @InjectMockKs
    lateinit var interactor: UpdateDescriptionProjectReportVerificationFile

    @BeforeEach
    fun setup() {
        clearMocks(fileService, filePersistence)
    }

    @Test
    fun updateDescription() {
        val description = "description"
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { fileService.setDescription(fileId = FILE_ID, description = description) } returns Unit

        assertDoesNotThrow { interactor.updateDescription(PROJECT_ID, REPORT_ID, FILE_ID, description) }
    }

    @Test
    fun `updateDescription - FileNotFound`() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns false
        assertThrows<FileNotFound> { interactor.updateDescription(PROJECT_ID, REPORT_ID, FILE_ID, "description") }
    }

    @Test
    fun `updateDescription - too long`() {
        val description = "A".repeat(251)

        assertThrows<AppInputValidationException> { interactor.updateDescription(PROJECT_ID, REPORT_ID, FILE_ID, description) }
    }
}
