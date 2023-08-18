package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverview
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class GetProjectReportVerificationWorkOverviewTest : UnitTest() {

    companion object {
        private const val REPORT_ID = 65L
    }

    @MockK
    private lateinit var calculator: GetProjectReportVerificationWorkOverviewCalculator

    @InjectMockKs
    lateinit var interactor: GetProjectReportVerificationWorkOverview

    @BeforeEach
    fun setup() {
        clearMocks(calculator)
    }

    @Test
    fun get() {
        val overview = mockk<VerificationWorkOverview>()
        every { calculator.getWorkOverviewPerPartner(REPORT_ID) } returns overview
        assertThat(interactor.get(REPORT_ID)).isEqualTo(overview)
    }

}
