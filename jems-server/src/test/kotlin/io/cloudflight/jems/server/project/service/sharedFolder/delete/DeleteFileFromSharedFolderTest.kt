package io.cloudflight.jems.server.project.service.sharedFolder.delete

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.sharedFolderFile.delete.DeleteFileFromSharedFolder
import io.cloudflight.jems.server.project.service.sharedFolderFile.delete.FileNotFound
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
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

        interactor.delete(PROJECT_ID, FILE_ID)

        verify(exactly = 1) { filePersistence.deleteFile(JemsFileType.SharedFolder, FILE_ID) }
    }

    @Test
    fun deleteFileNotFound() {
        every { filePersistence.existsFile(JemsFileType.SharedFolder, FILE_ID) } returns false
        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, FILE_ID) }
    }
}
