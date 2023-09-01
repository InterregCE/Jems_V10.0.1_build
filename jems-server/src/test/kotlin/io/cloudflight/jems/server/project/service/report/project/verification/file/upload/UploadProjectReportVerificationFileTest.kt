package io.cloudflight.jems.server.project.service.report.project.verification.file.upload

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
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
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.io.InputStream
import java.time.ZonedDateTime

class UploadProjectReportVerificationFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 91L
        const val REPORT_ID = 95L
        const val FILE_NAME = "new.pdf"
        fun filePath() = VerificationDocument.generatePath(PROJECT_ID, REPORT_ID)

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "test",
            callId = 1L,
            callName = "",
            acronym = "project",
            status = ApplicationStatus.CONTRACTED
        )

        private val jemsFile = JemsFile(
            id = 904L,
            name = FILE_NAME,
            type = VerificationDocument,
            uploaded = ZonedDateTime.now(),
            author = mockk(),
            size = 4L,
            description = "desc",
            indexedPath = "indexed/path",
        )

        private val fileMetadata = JemsFileMetadata(
            id = 904L,
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
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: UploadProjectReportVerificationFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence, projectReportFilePersistence, securityService, projectPersistence)
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

        every { securityService.getUserIdOrThrow() } returns userId
        every { projectReportFilePersistence.addAttachmentToProjectReport(capture(fileToAdd)) } returns jemsFile

        every { projectPersistence.throwIfNotExists(PROJECT_ID, any()) } returns Unit
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary

        every { auditPublisher.publishEvent(ofType(ProjectFileChangeEvent::class)) } returns Unit

        val changeEventSlot = slot<ProjectFileChangeEvent>()
        val uploadedFile = interactor.upload(PROJECT_ID, REPORT_ID, file)
        assertThat(uploadedFile).isEqualTo(fileMetadata.copy(uploaded = uploadedFile.uploaded))
        verify(exactly = 1) { auditPublisher.publishEvent(capture(changeEventSlot)) }
        assertThat(fileToAdd.captured).isEqualTo(
            JemsFileCreate(
                projectId = PROJECT_ID,
                partnerId = null,
                name = FILE_NAME,
                path = filePath(),
                type = VerificationDocument,
                size = file.size,
                content = file.stream,
                userId = userId,
                defaultDescription = "",
            )
        )

        assertThat(changeEventSlot.captured).isEqualTo(
            ProjectFileChangeEvent(FileChangeAction.Upload,
                projectSummary,
                jemsFile
            )
        )
    }

    @Test
    fun `upload - FileAlreadyExists`() {
        val fileName = "duplicate.pdf"
        val file = mockk<ProjectFile> { every { name } returns fileName }

        every { filePersistence.existsFile(exactPath = eq(filePath()), fileName = fileName) } returns true

        assertThrows<FileAlreadyExists> { interactor.upload(projectId = PROJECT_ID, reportId = REPORT_ID, file = file) }
    }

    @Test
    fun `upload - FileTypeNotSupported`() {
        val file = mockk<ProjectFile> { every { name } returns "file.jems" }

        assertThrows<FileTypeNotSupported> { interactor.upload(projectId = PROJECT_ID, reportId = REPORT_ID, file = file) }
    }
}
