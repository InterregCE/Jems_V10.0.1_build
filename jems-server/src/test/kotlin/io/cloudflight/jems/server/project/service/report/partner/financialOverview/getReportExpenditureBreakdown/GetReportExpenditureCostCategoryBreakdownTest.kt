package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.financialOverview.ExpenditureCostCategoryBreakdown
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetReportExpenditureCostCategoryBreakdownTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 590L
    }

    @MockK
    lateinit var reportExpenditureCostCategoryCalculatorService: GetReportExpenditureCostCategoryCalculatorService

    @InjectMockKs
    lateinit var interactor: GetReportExpenditureCostCategoryBreakdown

    @Test
    fun get() {
        val result = mockk<ExpenditureCostCategoryBreakdown>()

        every { reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(PARTNER_ID, reportId = 18L) } returns result

        assertThat(interactor.get(PARTNER_ID, reportId = 18L)).isEqualTo(result)
    }
}
