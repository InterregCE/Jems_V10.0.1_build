package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.fileManagement.FileNotFound
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
import org.junit.jupiter.api.assertThrows

internal class DeleteContractFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 480L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: DeleteContractFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
        clearMocks(reportFilePersistence)
    }

    @Test
    fun `delete contract file`() {
        every { reportFilePersistence.getFileType(18L, PROJECT_ID) } returns ProjectPartnerReportFileType.Contract
        every { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 18L) } answers { }
        interactor.delete(PROJECT_ID, fileId = 18L)
        verify(exactly =  1) { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 18L) }
    }

    @Test
    fun `delete contract doc file`() {
        every { reportFilePersistence.getFileType(19L, PROJECT_ID) } returns ProjectPartnerReportFileType.ContractDoc
        every { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 19L) } answers { }
        interactor.delete(PROJECT_ID, fileId = 19L)
        verify(exactly =  1) { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 19L) }
    }

    @Test
    fun `delete - not found`() {
        every { reportFilePersistence.getFileType(-1L, PROJECT_ID) } returns null
        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, fileId = -1L) }
    }

}
