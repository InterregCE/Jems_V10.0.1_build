package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdown
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetReportExpenditureUnitCostBreakdownTest : UnitTest() {

    @MockK
    lateinit var calculator: GetReportExpenditureUnitCostBreakdownCalculator

    @InjectMockKs
    lateinit var interactor: GetReportExpenditureUnitCostBreakdown

    @BeforeEach
    fun resetMocks() {
        clearMocks(calculator)
    }

    @Test
    fun get() {
        val result = mockk<ExpenditureUnitCostBreakdown>()
        every { calculator.get(partnerId = 15L, reportId = 150L) } returns result
        assertThat(interactor.get(15L, reportId = 150L)).isEqualTo(result)
    }

}
