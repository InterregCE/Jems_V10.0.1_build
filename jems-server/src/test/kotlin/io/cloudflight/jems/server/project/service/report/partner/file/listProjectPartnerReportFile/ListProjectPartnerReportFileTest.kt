package io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileSearchRequest
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
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
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var sensitiveDataAuthorization: SensitiveDataAuthorizationService

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence


    @InjectMockKs
    lateinit var interactor: ListProjectPartnerReportFile

    @Test
    fun `list PartnerReport files`() {
        testValidConfig(
            partnerId = 25L,
            reportId = 65L,
            type = JemsFileType.PartnerReport,
            filterSubtypes = setOf(JemsFileType.Activity, JemsFileType.ProcurementAttachment),
            expectedIndexSearch = "Project/000640/Report/Partner/000025/PartnerReport/000065/"
        )
    }

    @Test
    fun `list WorkPlan files`() {
        testValidConfig(
            partnerId = 26L,
            reportId = 66L,
            type = JemsFileType.WorkPlan,
            filterSubtypes = setOf(JemsFileType.Output),
            expectedIndexSearch = "Project/000640/Report/Partner/000026/PartnerReport/000066/WorkPlan/"
        )
    }

    @Test
    fun `list Expenditure files`() {
        testValidConfig(
            partnerId = 27L,
            reportId = 67L,
            type = JemsFileType.Expenditure,
            filterSubtypes = setOf(JemsFileType.Expenditure),
            expectedIndexSearch = "Project/000640/Report/Partner/000027/PartnerReport/000067/Expenditure/"
        )
    }

    @Test
    fun `list Procurement files`() {
        testValidConfig(
            partnerId = 28L,
            reportId = 68L,
            type = JemsFileType.Procurement,
            filterSubtypes = emptySet(),
            expectedIndexSearch = "Project/000640/Report/Partner/000028/PartnerReport/000068/Procurement/"
        )
    }

    @Test
    fun `list Contribution files`() {
        testValidConfig(
            partnerId = 29L,
            reportId = 69L,
            type = JemsFileType.Contribution,
            filterSubtypes = emptySet(),
            expectedIndexSearch = "Project/000640/Report/Partner/000029/PartnerReport/000069/Contribution/"
        )
    }

    @Test
    fun `test invalid filter`() {
        val searchRequest = JemsFileSearchRequest(
            reportId = 1L,
            treeNode = JemsFileType.Activity,
        )
        assertThrows<InvalidSearchConfiguration> { interactor.list(1L, Pageable.unpaged(), searchRequest) }
    }

    @Test
    fun `test invalid search filter config`() {
        val searchRequest = JemsFileSearchRequest(
            reportId = 1L,
            treeNode = JemsFileType.WorkPlan,
            // filter subtype is not actually a subtype of treeNode
            filterSubtypes = setOf(JemsFileType.Activity, JemsFileType.Procurement),
        )
        val ex = assertThrows<InvalidSearchFilterConfiguration> {
            interactor.list(1L, Pageable.unpaged(), searchRequest)
        }
        assertThat(ex.message).isEqualTo("Following filters cannot be applied: [Procurement]")
    }

    private fun testValidConfig(
        partnerId: Long,
        reportId: Long,
        type: JemsFileType,
        filterSubtypes: Set<JemsFileType>,
        expectedIndexSearch: String,
    ) {


        every { partnerPersistence.getProjectIdForPartnerId(partnerId, null) } returns PROJECT_ID
        val reportFile = mockk<JemsFile>()

        val indexPrefix = slot<String>()
        val filterSubtypesSlot = slot<Set<JemsFileType>>()
        val filterUsers = slot<Set<Long>>()

        every { sensitiveDataAuthorization.canViewPartnerSensitiveData(partnerId) } returns true
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, reportFile.id) }
        every { filePersistence.listAttachments(any(), capture(indexPrefix), capture(filterSubtypesSlot), capture(filterUsers)) } returns
            PageImpl(listOf(reportFile))

        val searchRequest = JemsFileSearchRequest(
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
