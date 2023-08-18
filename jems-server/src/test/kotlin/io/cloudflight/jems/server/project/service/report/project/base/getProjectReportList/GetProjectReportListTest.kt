package io.cloudflight.jems.server.project.service.report.project.base.getProjectReportList

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.getProjectReport.GetProjectReportTest.Companion.expectedReportSummary
import io.cloudflight.jems.server.project.service.report.project.base.getProjectReport.GetProjectReportTest.Companion.period
import io.cloudflight.jems.server.project.service.report.project.base.getProjectReport.GetProjectReportTest.Companion.report
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

internal class GetProjectReportListTest : UnitTest() {

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var certificateCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence
    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectReportList

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, certificateCoFinancingPersistence, projectPersistence)
        every { projectPersistence.getProjectPeriods(any(), "v4") } returns listOf(period)
        every { certificateCoFinancingPersistence.getTotalsForProjectReports(any())} returns mapOf(
            Pair(7L, BigDecimal.ONE),
            Pair(8L, BigDecimal.ONE)
        )
    }

    @ParameterizedTest(name = "findAll, when last report in {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft"])
    fun `findAll, when last report draft`(status: ProjectReportStatus) {
        val projectId = 91L + status.ordinal
        every { reportPersistence.listReports(projectId, Pageable.unpaged()) } returns PageImpl(
            listOf(
                report.copy(id = 7L, projectId = projectId, periodNumber = 7),
                report.copy(id = 8L, projectId = projectId, periodNumber = 8),
            )
        )

        assertThat(interactor.findAll(projectId, Pageable.unpaged())).containsExactly(
            expectedReportSummary.copy(id = 7L, deletable = true, totalEligibleAfterVerification = null),
            expectedReportSummary.copy(id = 8L, deletable = true, periodDetail = null, totalEligibleAfterVerification = null),
        )
    }

    @ParameterizedTest(name = "findAll, when last report not draft but {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `findAll, when last report not draft but`(status: ProjectReportStatus) {
        val projectId = 191L + status.ordinal
        every { reportPersistence.listReports(projectId, Pageable.unpaged()) } returns PageImpl(
            listOf(report.copy(id = 7L, projectId = projectId, periodNumber = 7))
        )

        assertThat(interactor.findAll(projectId, Pageable.unpaged()))
            .containsExactly(expectedReportSummary.copy(id = 7L, deletable = true, totalEligibleAfterVerification = null))
    }

}
