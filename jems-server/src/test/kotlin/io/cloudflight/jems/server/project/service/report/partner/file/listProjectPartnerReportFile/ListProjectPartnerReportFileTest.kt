package io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileSearchRequest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ListProjectPartnerReportFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 640L
    }

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: ListProjectPartnerReportFile

    @Test
    fun `list PartnerReport files`() {
        testValidConfig(
            partnerId = 25L,
            reportId = 65L,
            type = ProjectPartnerReportFileType.PartnerReport,
            filterSubtypes = setOf(ProjectPartnerReportFileType.Activity, ProjectPartnerReportFileType.ProcurementAttachment),
            expectedIndexSearch = "Project/000640/Report/Partner/000025/PartnerReport/000065/"
        )
    }

    @Test
    fun `list WorkPlan files`() {
        testValidConfig(
            partnerId = 26L,
            reportId = 66L,
            type = ProjectPartnerReportFileType.WorkPlan,
            filterSubtypes = setOf(ProjectPartnerReportFileType.Output),
            expectedIndexSearch = "Project/000640/Report/Partner/000026/PartnerReport/000066/WorkPlan/"
        )
    }

    @Test
    fun `list Expenditure files`() {
        testValidConfig(
            partnerId = 27L,
            reportId = 67L,
            type = ProjectPartnerReportFileType.Expenditure,
            filterSubtypes = setOf(ProjectPartnerReportFileType.Expenditure),
            expectedIndexSearch = "Project/000640/Report/Partner/000027/PartnerReport/000067/Expenditure/"
        )
    }

    @Test
    fun `list Procurement files`() {
        testValidConfig(
            partnerId = 28L,
            reportId = 68L,
            type = ProjectPartnerReportFileType.Procurement,
            filterSubtypes = emptySet(),
            expectedIndexSearch = "Project/000640/Report/Partner/000028/PartnerReport/000068/Procurement/"
        )
    }

    @Test
    fun `list Contribution files`() {
        testValidConfig(
            partnerId = 29L,
            reportId = 69L,
            type = ProjectPartnerReportFileType.Contribution,
            filterSubtypes = emptySet(),
            expectedIndexSearch = "Project/000640/Report/Partner/000029/PartnerReport/000069/Contribution/"
        )
    }

    @Test
    fun `test invalid filter`() {
        val searchRequest = ProjectReportFileSearchRequest(
            reportId = 1L,
            treeNode = ProjectPartnerReportFileType.Activity,
        )
        assertThrows<InvalidSearchConfiguration> { interactor.list(1L, Pageable.unpaged(), searchRequest) }
    }

    @Test
    fun `test invalid search filter config`() {
        val searchRequest = ProjectReportFileSearchRequest(
            reportId = 1L,
            treeNode = ProjectPartnerReportFileType.WorkPlan,
            // filter subtype is not actually a subtype of treeNode
            filterSubtypes = setOf(ProjectPartnerReportFileType.Activity, ProjectPartnerReportFileType.Procurement),
        )
        val ex = assertThrows<InvalidSearchFilterConfiguration> {
            interactor.list(1L, Pageable.unpaged(), searchRequest)
        }
        assertThat(ex.message).isEqualTo("Following filters cannot be applied: [Procurement]")
    }

    private fun testValidConfig(
        partnerId: Long,
        reportId: Long,
        type: ProjectPartnerReportFileType,
        filterSubtypes: Set<ProjectPartnerReportFileType>,
        expectedIndexSearch: String,
    ) {
        every { partnerPersistence.getProjectIdForPartnerId(partnerId, null) } returns PROJECT_ID
        val reportFile = mockk<ProjectReportFile>()

        val indexPrefix = slot<String>()
        val filterSubtypesSlot = slot<Set<ProjectPartnerReportFileType>>()
        val filterUsers = slot<Set<Long>>()
        every { reportFilePersistence.listAttachments(any(), capture(indexPrefix), capture(filterSubtypesSlot), capture(filterUsers)) } returns
            PageImpl(listOf(reportFile))

        val searchRequest = ProjectReportFileSearchRequest(
            reportId = reportId,
            treeNode = type,
            filterSubtypes = filterSubtypes,
        )

        assertThat(interactor.list(partnerId, Pageable.unpaged(), searchRequest).content)
            .containsExactly(reportFile)

        assertThat(indexPrefix.captured).isEqualTo(expectedIndexSearch)
        assertThat(filterSubtypesSlot.captured).containsExactlyElementsOf(filterSubtypes)
        assertThat(filterUsers.captured).isEmpty()
    }

}
