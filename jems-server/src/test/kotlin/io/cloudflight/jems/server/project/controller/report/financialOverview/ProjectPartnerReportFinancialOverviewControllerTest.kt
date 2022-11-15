package io.cloudflight.jems.server.project.controller.report.financialOverview

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCoFinancingBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureLumpSumBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureLumpSumBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.*
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureUnitCostBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureUnitCostBreakdownLineDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.controller.workpackage.ProjectWorkPackageInvestmentControllerTest.Companion.investmentId
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.GetReportExpenditureCoFinancingBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.GetReportExpenditureInvestmentsBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown.GetReportExpenditureLumpSumBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown.GetReportExpenditureUnitCostBreakdownInteractor
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

        private val dummyLineLumpSum = ExpenditureLumpSumBreakdownLine(
            reportLumpSumId = 36L,
            lumpSumId = 945L,
            name = setOf(InputTranslation(SystemLanguage.EN, "some lump sum 36 (or 945)")),
            period = 4,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            previouslyPaid = BigDecimal.ONE,
            currentReport = BigDecimal.ZERO,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
        )

        private val dummyInvestmentLine = ExpenditureInvestmentBreakdownLine(
            reportInvestmentId = 845L,
            investmentId = 22L,
            investmentNumber = 1,
            workPackageNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            currentReport = BigDecimal.ZERO,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
        )

        private val dummyLineUnitCost = ExpenditureUnitCostBreakdownLine(
            reportUnitCostId = 44L,
            unitCostId = 945L,
            name = setOf(InputTranslation(SystemLanguage.EN, "some unit cost 44 (or 945)")),
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

        private val dummyExpenditureLumpSum = ExpenditureLumpSumBreakdown(
            lumpSums = listOf(
                dummyLineLumpSum.copy(lumpSumId = 55L, reportLumpSumId = 633L),
                dummyLineLumpSum,
            ),
            total = dummyLineLumpSum,
        )

        private val dummyInvestmentBreakdown = ExpenditureInvestmentBreakdown(
            investments = listOf(dummyInvestmentLine),
            total = dummyInvestmentLine
        )

        private val dummyExpenditureUnitCost = ExpenditureUnitCostBreakdown(
            unitCosts = listOf(
                dummyLineUnitCost.copy(unitCostId = 49L, reportUnitCostId = 649L),
                dummyLineUnitCost,
            ),
            total = dummyLineUnitCost,
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

        private val expectedDummyLineLumpSum = ExpenditureLumpSumBreakdownLineDTO(
            reportLumpSumId = 36L,
            lumpSumId = 945L,
            name = setOf(InputTranslation(SystemLanguage.EN, "some lump sum 36 (or 945)")),
            period = 4,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            previouslyPaid = BigDecimal.ONE,
            currentReport = BigDecimal.ZERO,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
        )

        private val expectedDummyInvestmentLine = ExpenditureInvestmentBreakdownLineDTO(
            reportInvestmentId = 845L,
            investmentId = 22L,
            investmentNumber = 1,
            workPackageNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            currentReport = BigDecimal.ZERO,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
        )

        private val expectedDummyLineUnitCost = ExpenditureUnitCostBreakdownLineDTO(
            reportUnitCostId = 44L,
            unitCostId = 945L,
            name = setOf(InputTranslation(SystemLanguage.EN, "some unit cost 44 (or 945)")),
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

        private val expectedDummyExpenditureLumpSum = ExpenditureLumpSumBreakdownDTO(
            lumpSums = listOf(
                expectedDummyLineLumpSum.copy(lumpSumId = 55L, reportLumpSumId = 633L),
                expectedDummyLineLumpSum,
            ),
            total = expectedDummyLineLumpSum,
        )

        private val expectedDummyInvestmentBreakdown = ExpenditureInvestmentBreakdownDTO(
            investments = listOf(expectedDummyInvestmentLine),
            total = expectedDummyInvestmentLine
        )

        private val expectedDummyExpenditureUnitCost = ExpenditureUnitCostBreakdownDTO(
            unitCosts = listOf(
                expectedDummyLineUnitCost.copy(unitCostId = 49L, reportUnitCostId = 649L),
                expectedDummyLineUnitCost,
            ),
            total = expectedDummyLineUnitCost,
        )
    }

    @MockK
    lateinit var getReportExpenditureCoFinancingBreakdown: GetReportExpenditureCoFinancingBreakdownInteractor

    @MockK
    lateinit var getReportExpenditureCostCategoryBreakdown: GetReportExpenditureCostCategoryBreakdownInteractor

    @MockK
    lateinit var getReportExpenditureLumpSumBreakdown: GetReportExpenditureLumpSumBreakdownInteractor

    @MockK
    lateinit var getReportExpenditureInvestmentsBreakdownInteractor: GetReportExpenditureInvestmentsBreakdownInteractor

    @MockK
    lateinit var getReportExpenditureUnitCostBreakdown: GetReportExpenditureUnitCostBreakdownInteractor

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

    @Test
    fun getLumpSumBreakdown() {
        every { getReportExpenditureLumpSumBreakdown.get(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            dummyExpenditureLumpSum
        assertThat(controller.getLumpSumBreakdown(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyExpenditureLumpSum)
    }

    @Test
    fun getInvestmentsBreakdown() {
        every { getReportExpenditureInvestmentsBreakdownInteractor.get(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            dummyInvestmentBreakdown
        assertThat(controller.getInvestmentsBreakdown(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyInvestmentBreakdown)
    }

    @Test
    fun getUnitCostBreakdown() {
        every { getReportExpenditureUnitCostBreakdown.get(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            dummyExpenditureUnitCost
        assertThat(controller.getUnitCostBreakdown(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyExpenditureUnitCost)
    }

}
