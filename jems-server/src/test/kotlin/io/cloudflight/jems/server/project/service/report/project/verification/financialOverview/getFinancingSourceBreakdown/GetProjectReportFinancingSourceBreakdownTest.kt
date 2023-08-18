package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class GetProjectReportFinancingSourceBreakdownTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 64L
        private const val REPORT_ID = 63L
    }

    @MockK
    private lateinit var calculator: GetProjectReportFinancingSourceBreakdownCalculator

    @InjectMockKs
    lateinit var interactor: GetProjectReportFinancingSourceBreakdown

    @BeforeEach
    fun setup() {
        clearMocks(calculator)
    }

    @Test
    fun get() {
        val calculatorBreakdown = mockk<FinancingSourceBreakdown>()
        every { calculator.getFinancingSource(PROJECT_ID, REPORT_ID) } returns calculatorBreakdown
        assertThat(interactor.get(PROJECT_ID, REPORT_ID)).isEqualTo(calculatorBreakdown)
    }

}
