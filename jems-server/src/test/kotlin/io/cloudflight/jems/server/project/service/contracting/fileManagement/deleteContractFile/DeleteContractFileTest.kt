package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.fileManagement.FileNotFound
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeleteContractFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 480L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var validator: ContractingValidator

    @InjectMockKs
    lateinit var interactor: DeleteContractFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
        clearMocks(filePersistence)
    }

    @Test
    fun `delete contract file`() {
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } returns Unit
        every { filePersistence.getFileType(18L, PROJECT_ID) } returns JemsFileType.Contract
        every { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 18L) } answers { }
        interactor.delete(PROJECT_ID, fileId = 18L)
        verify(exactly =  1) { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 18L) }
    }

    @Test
    fun `delete contract doc file`() {
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } returns Unit
        every { filePersistence.getFileType(19L, PROJECT_ID) } returns JemsFileType.ContractDoc
        every { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 19L) } answers { }
        interactor.delete(PROJECT_ID, fileId = 19L)
        verify(exactly =  1) { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 19L) }
    }

    @Test
    fun `delete - not found`() {
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } returns Unit
        every { filePersistence.getFileType(-1L, PROJECT_ID) } returns null
        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, fileId = -1L) }
    }

    @Test
    fun `delete file - section locked`() {
        val exception = ContractingModificationDeniedException()
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } throws exception
        assertThrows<ContractingModificationDeniedException> { interactor.delete(PROJECT_ID, fileId = 19L) }
    }

}
