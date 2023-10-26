package io.cloudflight.jems.server.project.service.auditAndControl.file.upload

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.AuditControlNotOngoingException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.OverrideMockKs
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.io.InputStream
import java.time.ZonedDateTime

class UploadAuditControlFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 61L
        const val AUDIT_CONTROL_ID = 62L
        const val FILE_NAME = "auditcontrol.pdf"
        fun filePath() = JemsFileType.AuditControl.generatePath(PROJECT_ID, AUDIT_CONTROL_ID)

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "test",
            callId = 1L,
            callName = "",
            acronym = "project",
            status = ApplicationStatus.CONTRACTED
        )

        private val jemsFile = JemsFile(
            id = 64L,
            name = FILE_NAME,
            type = JemsFileType.AuditControl,
            uploaded = ZonedDateTime.now(),
            author = mockk(),
            size = 4L,
            description = "desc",
            indexedPath = "indexed/path",
        )

        private val fileMetadata = JemsFileMetadata(
            id = 64L,
            name = FILE_NAME,
            uploaded = ZonedDateTime.now(),
        )
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var projectReportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    var generalValidatorService: GeneralValidatorDefaultImpl = GeneralValidatorDefaultImpl()

    @InjectMockKs
    lateinit var auditAndControlValidator: ProjectAuditAndControlValidator

    @OverrideMockKs
    lateinit var interactor: UploadAuditControlFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence, projectReportFilePersistence, securityService, projectPersistence, auditControlPersistence, auditPublisher)
    }

    @Test
    fun upload() {
        val userId = 97L
        val fileToAdd = slot<JemsFileCreate>()

        val file = ProjectFile(
            stream = mockk<InputStream>(),
            name = FILE_NAME,
            size = 128L,
        )

        every { filePersistence.existsFile(exactPath = filePath(), fileName = file.name) } returns false
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns mockk {
            every { status } returns AuditStatus.Ongoing
        }
        every { securityService.getUserIdOrThrow() } returns userId
        every { projectReportFilePersistence.saveAuditControlFile(capture(fileToAdd)) } returns jemsFile

        every { projectPersistence.throwIfNotExists(PROJECT_ID, any()) } returns Unit
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary

        every { auditPublisher.publishEvent(ofType(ProjectFileChangeEvent::class)) } returns Unit

        val changeEventSlot = slot<ProjectFileChangeEvent>()
        val uploadedFile = interactor.upload(PROJECT_ID, AUDIT_CONTROL_ID, file)
        assertThat(uploadedFile).isEqualTo(fileMetadata.copy(uploaded = uploadedFile.uploaded))
        verify(exactly = 1) { auditPublisher.publishEvent(capture(changeEventSlot)) }
        assertThat(fileToAdd.captured).isEqualTo(
            JemsFileCreate(
                projectId = PROJECT_ID,
                partnerId = null,
                name = FILE_NAME,
                path = filePath(),
                type = JemsFileType.AuditControl,
                size = file.size,
                content = file.stream,
                userId = userId,
                defaultDescription = "",
            )
        )

        assertThat(changeEventSlot.captured).isEqualTo(ProjectFileChangeEvent(FileChangeAction.Upload, projectSummary, jemsFile))
    }

    @Test
    fun `upload - FileAlreadyExists`() {
        val fileName = "duplicate.pdf"
        val file = mockk<ProjectFile> { every { name } returns fileName }

        every { filePersistence.existsFile(exactPath = eq(filePath()), fileName = fileName) } returns true

        assertThrows<FileAlreadyExists> { interactor.upload(PROJECT_ID, AUDIT_CONTROL_ID, file) }
    }

    @Test
    fun `upload - FileTypeNotSupported`() {
        val file = mockk<ProjectFile> { every { name } returns "file.jems" }

        assertThrows<FileTypeNotSupported> { interactor.upload(PROJECT_ID, AUDIT_CONTROL_ID, file) }
    }

    @Test
    fun `upload - NotOngoing`() {
        val file = ProjectFile(
            stream = mockk<InputStream>(),
            name = FILE_NAME,
            size = 128L,
        )

        every { filePersistence.existsFile(exactPath = filePath(), fileName = file.name) } returns false
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns mockk {
            every { status } returns AuditStatus.Closed
        }

        assertThrows<AuditControlNotOngoingException> { interactor.upload(PROJECT_ID, AUDIT_CONTROL_ID, file) }
    }
}
