package io.cloudflight.jems.server.project.service.auditAndControl.file.updateDescription

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.AuditControlNotOngoingException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.OverrideMockKs
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class UpdateDescriptionAuditControlFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 85L
        const val AUDIT_CONTROL_ID = 87L
        const val FILE_ID = 89L
        fun filePath() = JemsFileType.AuditControl.generatePath(PROJECT_ID, AUDIT_CONTROL_ID)
    }

    @MockK
    lateinit var fileService: JemsProjectFileService

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    var generalValidator: GeneralValidatorDefaultImpl = GeneralValidatorDefaultImpl()

    @InjectMockKs
    lateinit var auditAndControlValidator: ProjectAuditAndControlValidator

    @OverrideMockKs
    lateinit var interactor: UpdateDescriptionAuditControlFile

    @BeforeEach
    fun setup() {
        clearMocks(fileService, filePersistence, auditControlPersistence)
    }

    @Test
    fun updateDescription() {
        val description = "description"
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { fileService.setDescription(fileId = FILE_ID, description = description) } returns Unit
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns mockk {
            every { status } returns AuditStatus.Ongoing
        }

        assertDoesNotThrow { interactor.updateDescription(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID, description) }
    }

    @Test
    fun `updateDescription - FileNotFound`() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns false
        assertThrows<FileNotFound> { interactor.updateDescription(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID, "description") }
    }

    @Test
    fun `updateDescription - too long`() {
        val description = "A".repeat(251)

        assertThrows<AppInputValidationException> { interactor.updateDescription(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID, description) }
    }

    @Test
    fun `updateDescription - NotOngoing`() {
        val description = "description"
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns mockk {
            every { status } returns AuditStatus.Closed
        }

        assertThrows<AuditControlNotOngoingException> { interactor.updateDescription(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID, description) }
    }

}
