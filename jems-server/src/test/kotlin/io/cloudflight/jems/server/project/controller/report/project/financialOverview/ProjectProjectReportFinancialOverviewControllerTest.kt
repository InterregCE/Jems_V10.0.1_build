package io.cloudflight.jems.server.project.controller.report.project.financialOverview

import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownLineDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.CertificateCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown.GetReportCertificateCoFinancingBreakdownInteractor
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
    }

    @MockK
    lateinit var getReportCertificateCoFinancingBreakdown: GetReportCertificateCoFinancingBreakdownInteractor

    @InjectMockKs
    private lateinit var controller: ProjectReportFinancialOverviewController

    @Test
    fun getCoFinancingBreakdown() {
        every { getReportCertificateCoFinancingBreakdown.get(projectId = PROJECT_ID, reportId = REPORT_ID) } returns
            dummyCertificateCoFinancing
        assertThat(controller.getCoFinancingBreakdown(projectId = PROJECT_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyCertificateCoFinancing)
    }
}
