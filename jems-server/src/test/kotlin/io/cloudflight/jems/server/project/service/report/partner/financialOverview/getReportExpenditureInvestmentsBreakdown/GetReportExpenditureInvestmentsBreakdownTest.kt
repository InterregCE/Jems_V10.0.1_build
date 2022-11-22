package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestmentsBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.GetReportExpenditureInvestmentsBreakdown
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.GetReportExpenditureInvestmentsBreakdownCalculator
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetReportExpenditureInvestmentsBreakdownTest : UnitTest() {

    @MockK
    lateinit var calculator: GetReportExpenditureInvestmentsBreakdownCalculator

    @InjectMockKs
    lateinit var interactor: GetReportExpenditureInvestmentsBreakdown

    @Test
    fun get() {
        val result = mockk<ExpenditureInvestmentBreakdown>()
        every { calculator.get(15L, reportId = 59L) } returns result
        assertThat(interactor.get(15L, reportId = 59L)).isEqualTo(result)
    }

}
