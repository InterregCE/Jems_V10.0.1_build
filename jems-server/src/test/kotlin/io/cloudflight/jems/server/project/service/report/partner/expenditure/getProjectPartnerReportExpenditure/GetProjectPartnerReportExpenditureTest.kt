package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetProjectPartnerReportExpenditureTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 520L
    }

    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence

    @InjectMockKs
    lateinit var getReportContribution: GetProjectPartnerReportExpenditure

    @Test
    fun getContribution() {
        val expenditure = mockk<ProjectPartnerReportExpenditureCost>()
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 74L) } returns
            listOf(expenditure)
        assertThat(getReportContribution.getExpenditureCosts(PARTNER_ID, reportId = 74L)).containsExactly(expenditure)
    }
}
