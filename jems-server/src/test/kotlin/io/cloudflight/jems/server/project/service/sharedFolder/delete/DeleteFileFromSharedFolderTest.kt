package io.cloudflight.jems.server.project.service.sharedFolder.delete

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.sharedFolderFile.delete.DeleteFileFromSharedFolder
import io.cloudflight.jems.server.project.service.sharedFolderFile.delete.FileNotFound
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

class DeleteFileFromSharedFolderTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 123L
        private const val FILE_ID = 456L

        private  val dummyFile = JemsFile(
            id = 15L,
            name = "attachment.pdf",
            type = JemsFileType.ControlDocument,
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
    private lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var eventPublisher: ApplicationEventPublisher


    @InjectMockKs
    private lateinit var interactor: DeleteFileFromSharedFolder

    @BeforeEach
    fun setUp() {
        clearMocks(filePersistence)
    }

    @Test
    fun delete() {
        every { filePersistence.existsFile(JemsFileType.SharedFolder, FILE_ID) } returns true
        every { filePersistence.deleteFile(JemsFileType.SharedFolder, FILE_ID) } returns Unit
        every { filePersistence.getFile(PROJECT_ID, FILE_ID) } returns dummyFile
        every { projectPersistence.getProjectSummary(PROJECT_ID) } answers { projectSummary }
        every { securityService.currentUser!!.user.email } returns "test@email.com"
        every { eventPublisher.publishEvent(ofType(ProjectFileChangeEvent::class)) } returns Unit

        val changeEventSlot = slot<ProjectFileChangeEvent>()
        interactor.delete(PROJECT_ID, FILE_ID)

        verify(exactly = 1) { filePersistence.deleteFile(JemsFileType.SharedFolder, FILE_ID) }
        verify(exactly = 1) { eventPublisher.publishEvent(capture(changeEventSlot)) }
    }

    @Test
    fun deleteFileNotFound() {
        every { filePersistence.getFile(PROJECT_ID, FILE_ID) } throws FileNotFound()
        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, FILE_ID) }
    }
}
