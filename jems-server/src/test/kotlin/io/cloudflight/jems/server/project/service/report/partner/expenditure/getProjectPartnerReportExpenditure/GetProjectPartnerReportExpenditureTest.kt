package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetProjectPartnerReportExpenditureTest : UnitTest() {

    @MockK
    lateinit var calculator: GetProjectPartnerReportExpenditureCalculator

    @InjectMockKs
    lateinit var getReportContribution: GetProjectPartnerReportExpenditure

    @Test
    fun getContribution() {
        val result = mockk<ProjectPartnerReportExpenditureCost>()
        every { calculator.getExpenditureCosts(partnerId = 1L, reportId = 44L) } returns listOf(result)
        assertThat(getReportContribution.getExpenditureCosts(1L, reportId = 44L)).containsExactly(result)
    }

}
