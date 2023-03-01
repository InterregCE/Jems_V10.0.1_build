package io.cloudflight.jems.server.project.service.contracting.fileManagement.deletePartnerFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeletePartnerFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 10L
        private const val FILE_ID = 20L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var validator: ContractingValidator

    @InjectMockKs
    lateinit var interactor: DeletePartnerFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
        clearMocks(filePersistence)
    }

    @Test
    fun `delete partner file`() {
        every { validator.validatePartnerLock(PARTNER_ID) } returns Unit
        every { filePersistence.getFileTypeByPartnerId(FILE_ID, PARTNER_ID) } returns JemsFileType.ContractPartnerDoc
        every { contractingFilePersistence.deleteFileByPartnerId(PARTNER_ID, FILE_ID) } answers { }
        interactor.delete(PARTNER_ID, FILE_ID)
        verify(exactly =  1) { contractingFilePersistence.deleteFileByPartnerId(PARTNER_ID, FILE_ID) }
    }

    @Test
    fun `delete file - section locked`() {
        val exception = ContractingModificationDeniedException()
        every { validator.validatePartnerLock(PARTNER_ID) } throws exception
        assertThrows<ContractingModificationDeniedException> { interactor.delete(PARTNER_ID, FILE_ID) }
    }

}
