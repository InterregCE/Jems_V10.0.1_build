package io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

internal class ListContractingFilesTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 450L
    }

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: ListContractingFiles

    @BeforeEach
    fun setup() {
        clearMocks(partnerPersistence)
        clearMocks(reportFilePersistence)
    }

    @Test
    fun `list contracting`() {
        val filters = setOf(ProjectPartnerReportFileType.Contract, ProjectPartnerReportFileType.ContractDoc)
        val indexPrefix = slot<String>()
        val result = mockk<Page<ProjectReportFile>>()
        every { reportFilePersistence.listAttachments(Pageable.unpaged(), capture(indexPrefix), filters, any()) } returns result

        val searchRequest = ProjectContractingFileSearchRequest(
            treeNode = ProjectPartnerReportFileType.Contracting,
            filterSubtypes = filters,
        )
        assertThat(interactor.list(PROJECT_ID, partnerId = null, Pageable.unpaged(), searchRequest)).isEqualTo(result)

        assertThat(indexPrefix.captured).isEqualTo("Project/000450/Contracting/")
    }

    @Test
    fun `list partner`() {
        every { partnerPersistence.getProjectIdForPartnerId(20L) } returns PROJECT_ID

        val filters = emptySet<ProjectPartnerReportFileType>()
        val indexPrefix = slot<String>()
        val result = mockk<Page<ProjectReportFile>>()
        every { reportFilePersistence.listAttachments(Pageable.unpaged(), capture(indexPrefix), filters, any()) } returns result

        val searchRequest = ProjectContractingFileSearchRequest(
            treeNode = ProjectPartnerReportFileType.ContractPartnerDoc,
            filterSubtypes = filters,
        )
        assertThat(interactor.list(PROJECT_ID, partnerId = 20L, Pageable.unpaged(), searchRequest)).isEqualTo(result)

        assertThat(indexPrefix.captured).isEqualTo("Project/000450/Contracting/ContractPartner/ContractPartnerDoc/000020/")
    }

    @ParameterizedTest(name = "list - invalid search config with {0}")
    @EnumSource(value = ProjectPartnerReportFileType::class, mode = EnumSource.Mode.EXCLUDE,
        names = ["Contracting", "ContractSupport", "Contract", "ContractDoc", "ContractPartner", "ContractPartnerDoc", "ContractInternal"],
    )
    fun `list - invalid search config`(invalidType: ProjectPartnerReportFileType) {
        val searchRequest = ProjectContractingFileSearchRequest(treeNode = invalidType, filterSubtypes = emptySet())
        assertThrows<InvalidSearchConfiguration> { interactor.list(PROJECT_ID, partnerId = null, Pageable.unpaged(), searchRequest) }
    }

    @Test
    fun `list - invalid filters`() {
        val searchRequest = ProjectContractingFileSearchRequest(
            treeNode = ProjectPartnerReportFileType.ContractSupport,
            filterSubtypes = setOf(ProjectPartnerReportFileType.ContractInternal),
        )
        assertThrows<InvalidSearchFilterConfiguration> { interactor.list(PROJECT_ID, partnerId = null, Pageable.unpaged(), searchRequest) }
    }

    @Test
    fun `list - missing partner id`() {
        val searchRequest = ProjectContractingFileSearchRequest(treeNode = ProjectPartnerReportFileType.ContractPartnerDoc, filterSubtypes = emptySet())
        assertThrows<InvalidSearchFilterPartnerWithoutId> { interactor.list(PROJECT_ID, partnerId = null, Pageable.unpaged(), searchRequest) }
    }

    @Test
    fun `list - wrong partner id`() {
        every { partnerPersistence.getProjectIdForPartnerId(25L) } returns 0L
        val searchRequest = ProjectContractingFileSearchRequest(treeNode = ProjectPartnerReportFileType.ContractPartnerDoc, filterSubtypes = emptySet())
        assertThrows<NullPointerException> { interactor.list(PROJECT_ID, partnerId = 25L, Pageable.unpaged(), searchRequest) }
    }

}
