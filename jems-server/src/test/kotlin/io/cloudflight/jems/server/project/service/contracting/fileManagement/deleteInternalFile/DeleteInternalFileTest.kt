package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteInternalFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.contracting.fileManagement.FileNotFound
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeleteInternalFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 480L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: DeleteInternalFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
        clearMocks(filePersistence)

    }


    @Test
    fun `delete internal file`() {
        every { filePersistence.getFileType(18L, PROJECT_ID) } returns JemsFileType.ContractInternal
        every { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 18L) } answers { }
        interactor.delete(PROJECT_ID, fileId = 18L)
        verify(exactly =  1) { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 18L) }
    }

    @Test
    fun `delete - not found`() {
        every { filePersistence.getFileType(-1L, PROJECT_ID) } returns null
        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, fileId = -1L) }
    }

}
