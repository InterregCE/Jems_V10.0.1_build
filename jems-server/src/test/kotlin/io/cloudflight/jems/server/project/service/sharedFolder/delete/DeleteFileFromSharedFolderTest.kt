package io.cloudflight.jems.server.project.service.sharedFolder.delete

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.sharedFolderFile.delete.DeleteFileFromSharedFolder
import io.cloudflight.jems.server.project.service.sharedFolderFile.delete.FileNotFound
import io.cloudflight.jems.server.project.service.sharedFolderFile.delete.UserIsNotOwnerOfFile
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteFileFromSharedFolderTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 123L
        private const val FILE_ID = 456L
    }

    @MockK
    private lateinit var filePersistence: JemsFilePersistence

    @MockK
    private lateinit var securityService: SecurityService

    @InjectMockKs
    private lateinit var interactor: DeleteFileFromSharedFolder

    @BeforeEach
    fun setUp() {
        clearMocks(filePersistence, securityService)
    }

    @Test
    fun delete() {
        val userId = 11L
        every { filePersistence.getProjectFileAuthor(PROJECT_ID, FILE_ID) } returns mockk { every { id } returns userId }
        every { securityService.getUserIdOrThrow() } returns userId
        every { filePersistence.deleteFile(JemsFileType.SharedFolder, FILE_ID) } returns Unit

        interactor.delete(PROJECT_ID, FILE_ID)

        verify(exactly = 1) { filePersistence.deleteFile(JemsFileType.SharedFolder, FILE_ID) }
    }

    @Test
    fun deleteFileNotFound() {
        every { filePersistence.getProjectFileAuthor(PROJECT_ID, FILE_ID) } returns null
        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, FILE_ID) }
    }

    @Test
    fun deleteUserIsNotOwner() {
        every { filePersistence.getProjectFileAuthor(PROJECT_ID, FILE_ID) } returns mockk { every { id } returns 1L }
        every { securityService.getUserIdOrThrow() } returns 2L

        assertThrows<UserIsNotOwnerOfFile> { interactor.delete(PROJECT_ID, FILE_ID) }
    }
}
