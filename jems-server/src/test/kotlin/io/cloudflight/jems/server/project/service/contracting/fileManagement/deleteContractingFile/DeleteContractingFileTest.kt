package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractingFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.authorization.ProjectContractInfoAuthorization
import io.cloudflight.jems.server.project.authorization.ProjectMonitoringAuthorization
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

internal class DeleteContractingFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 480L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var contractInfoAuth: ProjectContractInfoAuthorization

    @MockK
    lateinit var projectMonitoringAuthorization: ProjectMonitoringAuthorization

    @InjectMockKs
    lateinit var interactor: DeleteContractingFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
        clearMocks(reportFilePersistence)
        clearMocks(projectMonitoringAuthorization)
        clearMocks(contractInfoAuth)
    }

    @Test
    fun `delete contract file`() {
        every { contractingFilePersistence.existsFile(PROJECT_ID, fileId = 18L) } returns true
        every { reportFilePersistence.getFileType(18L, PROJECT_ID) } returns ProjectPartnerReportFileType.Contract
        every { contractInfoAuth.canEditContractInfo(PROJECT_ID) } returns true
        every { projectMonitoringAuthorization.canEditProjectMonitoring(PROJECT_ID) } returns false
        every { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 18L) } answers { }
        interactor.delete(PROJECT_ID, fileId = 18L)
        verify(exactly =  1) { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 18L) }
    }

    @Test
    fun `delete internal file`() {
        every { contractingFilePersistence.existsFile(PROJECT_ID, fileId = 18L) } returns true
        every { reportFilePersistence.getFileType(18L, PROJECT_ID) } returns ProjectPartnerReportFileType.ContractInternal
        every { contractInfoAuth.canEditContractInfo(PROJECT_ID) } returns false
        every { projectMonitoringAuthorization.canEditProjectMonitoring(PROJECT_ID) } returns true
        every { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 18L) } answers { }
        interactor.delete(PROJECT_ID, fileId = 18L)
        verify(exactly =  1) { contractingFilePersistence.deleteFile(PROJECT_ID, fileId = 18L) }
    }

    @Test
    fun `delete - not found`() {
        every { contractingFilePersistence.existsFile(PROJECT_ID, fileId = -1L) } returns false
        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, fileId = -1L) }
    }

}
