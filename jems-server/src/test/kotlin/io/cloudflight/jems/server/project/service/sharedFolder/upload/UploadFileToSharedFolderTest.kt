package io.cloudflight.jems.server.project.service.sharedFolder.upload

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.sharedFolderFile.upload.FileAlreadyExists
import io.cloudflight.jems.server.project.service.sharedFolderFile.upload.UploadFileToSharedFolder
import io.mockk.mockk
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import io.mockk.clearMocks
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.io.InputStream
import java.time.ZonedDateTime

class UploadFileToSharedFolderTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 456L
        private const val USER_ID = 789L
        private const val EXPECTED_PATH = "Project/000456/SharedFolder/"
        private const val FILE_NAME = "shared_folder_file.pdf"
        private val content = mockk<InputStream>()

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
            type = JemsFileType.SharedFolder,
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
    private lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var filePersistence: JemsFilePersistence

    @MockK
    private lateinit var projectFileService: JemsProjectFileService

    @MockK
    private lateinit var securityService: SecurityService

    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var interactor: UploadFileToSharedFolder

    @BeforeEach
    fun setUp() {
        clearMocks(projectPersistence, filePersistence, projectFileService, securityService)
    }

    @Test
    fun upload() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID, any()) } returns Unit
        every { filePersistence.existsFile(EXPECTED_PATH, FILE_NAME) } returns false
        val fileToAdd = slot<JemsFileCreate>()
        every { projectFileService.persistFile(capture(fileToAdd)) } returns jemsFile
        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { securityService.currentUser!!.user.email } returns "test@email.com"
        every { eventPublisher.publishEvent(ofType(ProjectFileChangeEvent::class)) } returns Unit

        val file = ProjectFile(
            stream = content,
            name = FILE_NAME,
            size = 5L
        )
        val changeEventSlot = slot<ProjectFileChangeEvent>()
        val uploadedFile = interactor.upload(PROJECT_ID, file)
        assertThat(uploadedFile).isEqualTo(fileMetadata.copy(uploaded = uploadedFile.uploaded))
        verify(exactly = 1) { eventPublisher.publishEvent(capture(changeEventSlot)) }

        assertThat(fileToAdd.captured).isEqualTo(
            JemsFileCreate(
                projectId = PROJECT_ID,
                partnerId = null,
                name = FILE_NAME,
                path = EXPECTED_PATH,
                type = JemsFileType.SharedFolder,
                size = 5L,
                content = content,
                userId = USER_ID
            )
        )

        assertThat(changeEventSlot.captured).isEqualTo(
            ProjectFileChangeEvent(FileChangeAction.Upload, projectSummary, jemsFile)
        )
    }

    @Test
    fun uploadFileAlreadyExists() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID, any()) } returns Unit
        every { filePersistence.existsFile(EXPECTED_PATH, FILE_NAME) } returns true
        val file = ProjectFile(
            stream = content,
            name = FILE_NAME,
            size = 5L
        )

        assertThrows<FileAlreadyExists> { interactor.upload(PROJECT_ID, file) }
    }

}
