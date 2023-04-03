package io.cloudflight.jems.server.project.service.sharedFolder.upload

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.sharedFolderFile.upload.FileAlreadyExists
import io.cloudflight.jems.server.project.service.sharedFolderFile.upload.UploadFileToSharedFolder
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream

class UploadFileToSharedFolderTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 456L
        private const val USER_ID = 789L
        private const val EXPECTED_PATH = "Project/000456/SharedFolder/"
        private const val FILE_NAME = "shared_folder_file.pdf"
        private val content = mockk<InputStream>()
    }

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var filePersistence: JemsFilePersistence

    @MockK
    private lateinit var projectFileService: JemsProjectFileService

    @MockK
    private lateinit var securityService: SecurityService

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
        val mockResult = mockk<JemsFileMetadata>()
        every { projectFileService.persistProjectFile(capture(fileToAdd)) } returns mockResult
        every { securityService.getUserIdOrThrow() } returns USER_ID

        val file = ProjectFile(
            stream = content,
            name = FILE_NAME,
            size = 5L
        )

        Assertions.assertThat(interactor.upload(PROJECT_ID, file)).isEqualTo(mockResult)
        Assertions.assertThat(fileToAdd.captured).isEqualTo(
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
