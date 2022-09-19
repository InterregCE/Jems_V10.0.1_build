package io.cloudflight.jems.server.project.service.contracting.fileManagement.uploadFileToContracting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UploadFileToContractingTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 440L
        private const val USER_ID = 8L
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence
    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadFileToContracting

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
        val summary = mockk<ProjectSummary>()
        every { summary.status } returns ApplicationStatus.CONTRACTED
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun uploadContract() {
        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 50L)
        testUpload(
            { interactor.uploadContract(PROJECT_ID, file) },
            expectedPath = "Project/000440/Contracting/ContractSupport/Contract/",
            expectedType = ProjectPartnerReportFileType.Contract,
        )
    }

    @Test
    fun uploadContractDocument() {
        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 50L)
        testUpload(
            { interactor.uploadContractDocument(PROJECT_ID, file) },
            expectedPath = "Project/000440/Contracting/ContractSupport/ContractDoc/",
            expectedType = ProjectPartnerReportFileType.ContractDoc,
        )
    }

    @Test
    fun uploadContractPartnerFile() {
        every { partnerPersistence.getProjectIdForPartnerId(60L) } returns PROJECT_ID
        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 50L)
        testUpload(
            { interactor.uploadContractPartnerFile(PROJECT_ID, partnerId = 60L, file) },
            expectedPath = "Project/000440/Contracting/ContractPartner/ContractPartnerDoc/000060/",
            expectedType = ProjectPartnerReportFileType.ContractPartnerDoc,
            expectedPartnerId = 60L,
        )
    }

    @Test
    fun uploadContractInternalFile() {
        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 50L)
        testUpload(
            { interactor.uploadContractInternalFile(PROJECT_ID, file) },
            expectedPath = "Project/000440/Contracting/ContractInternal/",
            expectedType = ProjectPartnerReportFileType.ContractInternal,
        )
    }

    private fun testUpload(
        testFunction: () -> ProjectReportFileMetadata,
        expectedPath: String,
        expectedType: ProjectPartnerReportFileType,
        expectedPartnerId: Long? = null,
    ) {
        val result = mockk<ProjectReportFileMetadata>()

        val fileSlot = slot<ProjectReportFileCreate>()
        every { contractingFilePersistence.uploadFile(capture(fileSlot)) } returns result

        assertThat(testFunction.invoke()).isEqualTo(result)

        with(fileSlot.captured) {
            assertThat(projectId).isEqualTo(PROJECT_ID)
            assertThat(partnerId).isEqualTo(expectedPartnerId)
            assertThat(name).isEqualTo("file_name.xlsx")
            assertThat(path).isEqualTo(expectedPath)
            assertThat(type).isEqualTo(expectedType)
            assertThat(size).isEqualTo(50L)
            assertThat(userId).isEqualTo(USER_ID)
        }
    }


    @Test
    fun `uploadContractPartnerFile - not existing`() {
        every { partnerPersistence.getProjectIdForPartnerId(666L) } returns 22L
        val file = mockk<ProjectFile>()

        assertThrows<PartnerNotFound> { interactor.uploadContractPartnerFile(PROJECT_ID, partnerId = 666L, file) }
    }

    @Test
    fun `uploadContract - wrong status`() {
        val summary = mockk<ProjectSummary>()
        every { summary.status } returns ApplicationStatus.DRAFT
        every { projectPersistence.getProjectSummary(10L) } returns summary

        assertThrows<ProjectNotApprovedException> { interactor.uploadContract(10L, mockk()) }
    }

    @Test
    fun `uploadContract - wrong file type`() {
        val file = mockk<ProjectFile>()
        every { file.name } returns "virus.exe"
        assertThrows<FileTypeNotSupported> { interactor.uploadContract(PROJECT_ID, file) }
    }

}
