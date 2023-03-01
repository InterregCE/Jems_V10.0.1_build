package io.cloudflight.jems.server.project.service.contracting.fileManagement.uploadFileToContracting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
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
    lateinit var securityService: SecurityService
    @MockK
    lateinit var filePersistence: JemsFilePersistence
    @MockK
    lateinit var fileRepository: JemsProjectFileService

    @RelaxedMockK
    lateinit var validator: ContractingValidator

    @InjectMockKs
    lateinit var interactor: UploadFileToContracting

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence, fileRepository)
        val summary = mockk<ProjectSummary>()
        every { summary.status } returns ApplicationStatus.CONTRACTED
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { filePersistence.existsFile(any(), "file_name.xlsx") } returns false
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun uploadContract() {
        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 50L)
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } returns Unit
        testUpload(
            { interactor.uploadContract(PROJECT_ID, file) },
            expectedPath = "Project/000440/Contracting/ContractSupport/Contract/",
            expectedType = JemsFileType.Contract,
        )
    }

    @Test
    fun uploadContractDocument() {
        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 50L)
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } returns Unit
        testUpload(
            { interactor.uploadContractDocument(PROJECT_ID, file) },
            expectedPath = "Project/000440/Contracting/ContractSupport/ContractDoc/",
            expectedType = JemsFileType.ContractDoc,
        )
    }

    @Test
    fun uploadContractPartnerFile() {
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } returns Unit
        every { partnerPersistence.getProjectIdForPartnerId(60L) } returns PROJECT_ID
        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 50L)
        testUpload(
            { interactor.uploadContractPartnerFile(PROJECT_ID, partnerId = 60L, file) },
            expectedPath = "Project/000440/Contracting/ContractPartner/ContractPartnerDoc/000060/",
            expectedType = JemsFileType.ContractPartnerDoc,
            expectedPartnerId = 60L,
        )
    }

    @Test
    fun uploadContractInternalFile() {
        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 50L)
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } returns Unit
        testUpload(
            { interactor.uploadContractInternalFile(PROJECT_ID, file) },
            expectedPath = "Project/000440/Contracting/ContractInternal/",
            expectedType = JemsFileType.ContractInternal,
        )
    }

    private fun testUpload(
        testFunction: () -> JemsFileMetadata,
        expectedPath: String,
        expectedType: JemsFileType,
        expectedPartnerId: Long? = null,
    ) {
        val result = mockk<JemsFileMetadata>()

        val fileSlot = slot<JemsFileCreate>()
        every { fileRepository.persistProjectFile(capture(fileSlot)) } returns result

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
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } returns Unit
        every { partnerPersistence.getProjectIdForPartnerId(666L) } returns 22L
        val file = mockk<ProjectFile>()

        assertThrows<PartnerNotFound> { interactor.uploadContractPartnerFile(PROJECT_ID, partnerId = 666L, file) }
    }

    @Test
    fun `uploadContract - wrong status`() {
        val summary = mockk<ProjectSummary>()
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } just Runs
        every { summary.status } returns ApplicationStatus.DRAFT
        every { projectPersistence.getProjectSummary(10L) } returns summary

        assertThrows<ProjectNotApprovedException> { interactor.uploadContract(10L, mockk()) }
    }

    @Test
    fun `uploadContract - wrong file type`() {
        val file = mockk<ProjectFile>()
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } returns Unit
        every { file.name } returns "virus.exe"
        assertThrows<FileTypeNotSupported> { interactor.uploadContract(PROJECT_ID, file) }
    }

    @Test
    fun `uploadContract - file already exists`() {
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } returns Unit
        every { filePersistence.existsFile("Project/000440/Contracting/ContractSupport/Contract/", "already-there.xlsx") } returns true

        val file = mockk<ProjectFile>()
        every { file.name } returns "already-there.xlsx"
        assertThrows<FileAlreadyExists> { interactor.uploadContract(PROJECT_ID, file) }
    }

    @Test
    fun `uploadContract - section locked`() {
        val file = mockk<ProjectFile>()
        val exception = ContractingModificationDeniedException()
        every { file.name } returns "test-file.xlsx"
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, PROJECT_ID) } throws exception

        assertThrows<ContractingModificationDeniedException> {
            interactor.uploadContract(PROJECT_ID, file)
        }
    }
}
