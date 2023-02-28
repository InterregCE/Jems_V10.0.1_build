package io.cloudflight.jems.server.project.controller.report.project.financialOverview

import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCostCategoryBreakdownLineDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown.GetReportCertificateCoFinancingBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown.GetReportCertificateCostCategoryBreakdownInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectProjectReportFinancialOverviewControllerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 526L
        private const val REPORT_ID = 606L

        private val dummyLineCoFin = CertificateCoFinancingBreakdownLine(
            fundId = null,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            previouslyPaid = BigDecimal.ONE,
            currentReport = BigDecimal.ZERO,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
        )

        private val dummyCertificateCoFinancing = CertificateCoFinancingBreakdown(
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


        private val expectedDummyLineCoFin = CertificateCoFinancingBreakdownLineDTO(
            fundId = null,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            previouslyPaid = BigDecimal.ONE,
            currentReport = BigDecimal.ZERO,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
        )

        private val expectedDummyCertificateCoFinancing = CertificateCoFinancingBreakdownDTO(
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

        private val dummyCostCategoryLine = CertificateCostCategoryBreakdownLine(
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.ONE,
        )

        private val dummyCostCategory = CertificateCostCategoryBreakdown(
            staff = dummyCostCategoryLine,
            office = dummyCostCategoryLine,
            travel = dummyCostCategoryLine,
            external = dummyCostCategoryLine,
            equipment = dummyCostCategoryLine,
            infrastructure = dummyCostCategoryLine,
            other = dummyCostCategoryLine,
            lumpSum = dummyCostCategoryLine,
            unitCost = dummyCostCategoryLine,
            total = dummyCostCategoryLine,
        )

        private val expectedDummyCostCategoryLine = CertificateCostCategoryBreakdownLineDTO(
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.ONE,
        )

        private val expectedDummyCostCategory = CertificateCostCategoryBreakdownDTO(
            staff = expectedDummyCostCategoryLine,
            office = expectedDummyCostCategoryLine,
            travel = expectedDummyCostCategoryLine,
            external = expectedDummyCostCategoryLine,
            equipment = expectedDummyCostCategoryLine,
            infrastructure = expectedDummyCostCategoryLine,
            other = expectedDummyCostCategoryLine,
            lumpSum = expectedDummyCostCategoryLine,
            unitCost = expectedDummyCostCategoryLine,
            total = expectedDummyCostCategoryLine,
        )
    }

    @MockK
    lateinit var getReportCertificateCoFinancingBreakdown: GetReportCertificateCoFinancingBreakdownInteractor

    @MockK
    lateinit var getReportCertificateCostCategoryBreakdownInteractor: GetReportCertificateCostCategoryBreakdownInteractor

    @InjectMockKs
    private lateinit var controller: ProjectReportFinancialOverviewController

    @Test
    fun getCoFinancingBreakdown() {
        every { getReportCertificateCoFinancingBreakdown.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns
            dummyCertificateCoFinancing
        assertThat(controller.getCoFinancingBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyCertificateCoFinancing)
    }

    @Test
    fun getCostCategoriesBreakdown() {
        every { getReportCertificateCostCategoryBreakdownInteractor.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns
            dummyCostCategory
        assertThat(controller.getCostCategoriesBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyCostCategory)
    }
}
