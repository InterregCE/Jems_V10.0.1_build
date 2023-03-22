package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetReportCertificateCostCategoryBreakdownTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 10L
        private const val REPORT_ID = 20L
    }

    @MockK
    private lateinit var reportCertificateCostCategoryCalculatorService: GetReportCertificateCostCategoryCalculatorService

    @InjectMockKs
    private lateinit var interactor: GetReportCertificateCostCategoryBreakdown

    @BeforeEach
    fun reset() {
        clearMocks(reportCertificateCostCategoryCalculatorService)
    }

    @Test
    fun get() {
        val result = mockk<CertificateCostCategoryBreakdown>()
        every { reportCertificateCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(PROJECT_ID, REPORT_ID) } returns result
        assertThat(interactor.get(PROJECT_ID, REPORT_ID)).isEqualTo(result)
    }

}
