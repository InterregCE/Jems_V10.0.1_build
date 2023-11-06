package io.cloudflight.jems.server.project.service.auditAndControl.file.delete

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.AuditControlNotOngoingException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class DeleteAuditControlFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 41L
        const val AUDIT_CONTROL_ID = 40L
        const val FILE_ID = 39L
        fun filePath() = JemsFileType.AuditControl.generatePath(PROJECT_ID, AUDIT_CONTROL_ID)

        private  val dummyFile = JemsFile(
            id = 15L,
            name = "attachment.pdf",
            type = JemsFileType.AuditControl,
            uploaded = ZonedDateTime.now(),
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            description = "desc",
            indexedPath = ""
        )

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 1L,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED
        )
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorDefaultImpl

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    lateinit var auditAndControlValidator: ProjectAuditAndControlValidator

    @InjectMockKs
    lateinit var interactor: DeleteAuditControlFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence, projectPersistence, auditPublisher, auditControlPersistence)
    }

    @Test()
    fun delete() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { filePersistence.deleteFile(JemsFileType.AuditControl, FILE_ID) } returns Unit
        every { filePersistence.getFile(PROJECT_ID, FILE_ID) } returns dummyFile
        every { projectPersistence.getProjectSummary(PROJECT_ID) } answers { projectSummary }
        every { auditPublisher.publishEvent(ofType(ProjectFileChangeEvent::class)) } returns Unit
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns mockk {
            every { status } returns AuditStatus.Ongoing
        }

        val changeEventSlot = slot<ProjectFileChangeEvent>()
        interactor.delete(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID)

        verify(exactly = 1) { filePersistence.deleteFile(JemsFileType.AuditControl, FILE_ID) }
        verify(exactly = 1) { auditPublisher.publishEvent(capture(changeEventSlot)) }
    }

    @Test()
    fun `delete - FileNotFound`() {
        every { filePersistence.getFile(PROJECT_ID, FILE_ID) } throws FileNotFound()

        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID) }
    }

    @Test()
    fun `delete - NotOngoing`() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { filePersistence.getFile(PROJECT_ID, FILE_ID) } returns dummyFile
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns mockk {
            every { status } returns AuditStatus.Closed
        }
        assertThrows<AuditControlNotOngoingException> { interactor.delete(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID) }
    }
}
