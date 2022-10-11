package io.cloudflight.jems.server.project.controller.report.financialOverview

import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCoFinancingBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownLineDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ExpenditureCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.GetReportExpenditureCoFinancingBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryBreakdownInteractor
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

        private val dummyLineCoFin = ExpenditureCoFinancingBreakdownLine(
            fundId = null,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            previouslyPaid = BigDecimal.ONE,
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

        private val dummyExpenditureCoFinancing = ExpenditureCoFinancingBreakdown(
            funds = listOf(
                dummyLineCoFin.copy(fundId = 7L),
                dummyLineCoFin,
            ),
            partnerContribution = dummyLineCoFin,
            publicContribution = dummyLineCoFin,
            automaticPublicContribution = dummyLineCoFin,
            privateContribution = dummyLineCoFin,
            total = dummyLineCoFin,
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

        private val expectedDummyLineCoFin = ExpenditureCoFinancingBreakdownLineDTO(
            fundId = null,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            previouslyPaid = BigDecimal.ONE,
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

        private val expectedDummyExpenditureCoFinancing = ExpenditureCoFinancingBreakdownDTO(
            funds = listOf(
                expectedDummyLineCoFin.copy(fundId = 7L),
                expectedDummyLineCoFin,
            ),
            partnerContribution = expectedDummyLineCoFin,
            publicContribution = expectedDummyLineCoFin,
            automaticPublicContribution = expectedDummyLineCoFin,
            privateContribution = expectedDummyLineCoFin,
            total = expectedDummyLineCoFin,
        )
    }

    @MockK
    lateinit var getReportExpenditureCoFinancingBreakdown: GetReportExpenditureCoFinancingBreakdownInteractor

    @MockK
    lateinit var getReportExpenditureCostCategoryBreakdown: GetReportExpenditureCostCategoryBreakdownInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportFinancialOverviewController

    @Test
    fun getCoFinancingBreakdown() {
        every { getReportExpenditureCoFinancingBreakdown.get(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            dummyExpenditureCoFinancing
        assertThat(controller.getCoFinancingBreakdown(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyExpenditureCoFinancing)
    }

    @Test
    fun getCostCategoriesBreakdown() {
        every { getReportExpenditureCostCategoryBreakdown.get(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            dummyExpenditureCostCategory
        assertThat(controller.getCostCategoriesBreakdown(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyExpenditureCostCategory)
    }

}
