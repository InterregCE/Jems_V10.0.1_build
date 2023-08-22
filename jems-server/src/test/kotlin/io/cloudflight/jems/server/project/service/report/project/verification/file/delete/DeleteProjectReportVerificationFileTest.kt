package io.cloudflight.jems.server.project.service.report.project.verification.file.delete

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class DeleteProjectReportVerificationFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 51L
        const val REPORT_ID = 55L
        const val FILE_ID = 59L
        fun filePath() = VerificationDocument.generatePath(PROJECT_ID, REPORT_ID)

        private  val dummyFile = JemsFile(
            id = 15L,
            name = "attachment.pdf",
            type = VerificationDocument,
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

    @InjectMockKs
    lateinit var interactor: DeleteProjectReportVerificationFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence)
    }

    @Test()
    fun delete() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { filePersistence.deleteFile(VerificationDocument, FILE_ID) } returns Unit
        every { filePersistence.getFile(PROJECT_ID, FILE_ID) } returns dummyFile
        every { projectPersistence.getProjectSummary(PROJECT_ID) } answers { projectSummary }
        every { auditPublisher.publishEvent(ofType(ProjectFileChangeEvent::class)) } returns Unit

        val changeEventSlot = slot<ProjectFileChangeEvent>()
        interactor.delete(PROJECT_ID, REPORT_ID, FILE_ID)

        verify(exactly = 1) { filePersistence.deleteFile(VerificationDocument, FILE_ID) }
        verify(exactly = 1) { auditPublisher.publishEvent(capture(changeEventSlot)) }
    }

    @Test()
    fun `delete - FileNotFound`() {
        every { filePersistence.getFile(PROJECT_ID, FILE_ID) } throws FileNotFound()

        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, REPORT_ID, FILE_ID) }
    }
}
