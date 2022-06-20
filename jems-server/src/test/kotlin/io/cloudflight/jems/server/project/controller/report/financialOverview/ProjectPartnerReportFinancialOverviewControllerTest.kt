package io.cloudflight.jems.server.project.controller.report.financialOverview

import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownLineDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.financialOverview.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryBreakdown
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectPartnerReportFinancialOverviewControllerTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 526L
        private const val REPORT_ID = 606L

        private val dummyLine = ExpenditureCostCategoryBreakdownLine(
            flatRate = 4,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            currentReport = BigDecimal.ZERO,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
        )

        private val dummyExpenditureCostCategory = ExpenditureCostCategoryBreakdown(
            staff = dummyLine,
            office = dummyLine,
            travel = dummyLine,
            external = dummyLine,
            equipment = dummyLine,
            infrastructure = dummyLine,
            other = dummyLine,
            lumpSum = dummyLine,
            unitCost = dummyLine,
            total = dummyLine,
        )

        private val expectedDummyLine = ExpenditureCostCategoryBreakdownLineDTO(
            flatRate = 4,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            currentReport = BigDecimal.ZERO,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
        )

        private val expectedDummyExpenditureCostCategory = ExpenditureCostCategoryBreakdownDTO(
            staff = expectedDummyLine,
            office = expectedDummyLine,
            travel = expectedDummyLine,
            external = expectedDummyLine,
            equipment = expectedDummyLine,
            infrastructure = expectedDummyLine,
            other = expectedDummyLine,
            lumpSum = expectedDummyLine,
            unitCost = expectedDummyLine,
            total = expectedDummyLine,
        )
    }

    @MockK
    lateinit var getReportExpenditureCostCategoryBreakdown: GetReportExpenditureCostCategoryBreakdown

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportFinancialOverviewController

    @Test
    fun getCostCategoriesBreakdown() {
        every { getReportExpenditureCostCategoryBreakdown.get(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            dummyExpenditureCostCategory
        assertThat(controller.getCostCategoriesBreakdown(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyExpenditureCostCategory)
    }

}
