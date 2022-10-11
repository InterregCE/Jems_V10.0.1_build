package io.cloudflight.jems.server.project.service.contracting.fileManagement.deletePartnerFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DeletePartnerFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 10L
        private const val FILE_ID = 20L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: DeletePartnerFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
        clearMocks(reportFilePersistence)
    }

    @Test
    fun `delete partner file`() {
        every { reportFilePersistence.getFileTypeByPartnerId(FILE_ID, PARTNER_ID) } returns ProjectPartnerReportFileType.ContractPartnerDoc
        every { contractingFilePersistence.deleteFileByPartnerId(PARTNER_ID, FILE_ID) } answers { }
        interactor.delete(PARTNER_ID, FILE_ID)
        verify(exactly =  1) { contractingFilePersistence.deleteFileByPartnerId(PARTNER_ID, FILE_ID) }
    }

}
