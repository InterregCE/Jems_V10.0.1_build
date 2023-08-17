package io.cloudflight.jems.server.plugin.services.project

import io.cloudflight.jems.server.plugin.services.report.ProjectReportVerificationDataProviderImpl
import io.cloudflight.jems.server.project.service.report.project.verification.ProjectReportVerificationPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.GetProjectReportFinancingSourceBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview.GetProjectReportVerificationWorkOverviewCalculator
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectReportVerificationDataProviderImplTest {

    companion object {
        const val PROJECT_ID = 71L
        const val REPORT_ID = 98L
    }

    @MockK
    lateinit var verificationPersistence: ProjectReportVerificationPersistence

    @MockK
    lateinit var expenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence

    @MockK
    lateinit var getFinancingSourceBreakdownCalculator: GetProjectReportFinancingSourceBreakdownCalculator

    @MockK
    lateinit var getProjectReportVerificationWorkOverviewCalculator: GetProjectReportVerificationWorkOverviewCalculator

    @InjectMockKs
    lateinit var dataProvider: ProjectReportVerificationDataProviderImpl

    @BeforeEach
    fun setup() {
        clearMocks(
            verificationPersistence,
            expenditureVerificationPersistence,
            getFinancingSourceBreakdownCalculator,
            getProjectReportVerificationWorkOverviewCalculator,
        )
    }

    @Test
    fun getClarifications() {
        val reportId = slot<Long>()
        every { verificationPersistence.getVerificationClarifications(reportId = capture(reportId)) } returns mockk(relaxed = true)

        dataProvider.getClarifications(projectId = PROJECT_ID, reportId = REPORT_ID)

        verify { verificationPersistence.getVerificationClarifications(reportId = REPORT_ID) }
        assertThat(reportId.captured).isEqualTo(REPORT_ID)
    }

    @Test
    fun getConclusion() {
        val projectId = slot<Long>()
        val reportId = slot<Long>()
        every { verificationPersistence.getVerificationConclusion(projectId = capture(projectId), reportId = capture(reportId)) } returns mockk(relaxed = true)

        dataProvider.getConclusion(projectId = PROJECT_ID, reportId = REPORT_ID)

        verify { verificationPersistence.getVerificationConclusion(projectId = PROJECT_ID, reportId = REPORT_ID) }
        assertThat(projectId.captured).isEqualTo(PROJECT_ID)
        assertThat(reportId.captured).isEqualTo(REPORT_ID)
    }

    @Test
    fun getExpenditureVerification() {
        val reportId = slot<Long>()
        every { expenditureVerificationPersistence.getProjectReportExpenditureVerification(projectReportId = capture(reportId)) } returns mockk(relaxed = true)

        dataProvider.getExpenditureVerification(projectId = PROJECT_ID, reportId = REPORT_ID)

        verify { expenditureVerificationPersistence.getProjectReportExpenditureVerification(projectReportId = REPORT_ID) }
        assertThat(reportId.captured).isEqualTo(REPORT_ID)
    }

    @Test
    fun getExpenditureVerificationRiskBased() {
        val projectId = slot<Long>()
        val reportId = slot<Long>()
        every {
            expenditureVerificationPersistence.getExpenditureVerificationRiskBasedData(
                projectId = capture(projectId),
                projectReportId = capture(reportId)
            )
        } returns mockk(relaxed = true)

        dataProvider.getExpenditureVerificationRiskBased(projectId = PROJECT_ID, reportId = REPORT_ID)

        verify { expenditureVerificationPersistence.getExpenditureVerificationRiskBasedData(projectId = PROJECT_ID, projectReportId = REPORT_ID) }
        assertThat(projectId.captured).isEqualTo(PROJECT_ID)
        assertThat(reportId.captured).isEqualTo(REPORT_ID)
    }

    @Test
    fun getFinancingSourceBreakdown() {
        val projectId = slot<Long>()
        val reportId = slot<Long>()
        every {
            getFinancingSourceBreakdownCalculator.getFinancingSource(
                projectId = capture(projectId),
                reportId = capture(reportId)
            )
        } returns mockk(relaxed = true)

        dataProvider.getFinancingSourceBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID)

        verify { getFinancingSourceBreakdownCalculator.getFinancingSource(projectId = PROJECT_ID, reportId = REPORT_ID) }
        assertThat(projectId.captured).isEqualTo(PROJECT_ID)
        assertThat(reportId.captured).isEqualTo(REPORT_ID)
    }

    @Test
    fun getVerificationWorkOverview() {
        val reportId = slot<Long>()
        every { getProjectReportVerificationWorkOverviewCalculator.getWorkOverviewPerPartner(reportId = capture(reportId)) } returns mockk(relaxed = true)

        dataProvider.getVerificationWorkOverview(projectId = PROJECT_ID, reportId = REPORT_ID)

        verify { getProjectReportVerificationWorkOverviewCalculator.getWorkOverviewPerPartner(reportId = REPORT_ID) }
        assertThat(reportId.captured).isEqualTo(REPORT_ID)
    }

}
