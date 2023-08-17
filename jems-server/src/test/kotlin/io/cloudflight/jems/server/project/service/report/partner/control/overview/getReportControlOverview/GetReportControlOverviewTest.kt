package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class GetReportControlOverviewTest: UnitTest() {

    companion object {
        private const val PARTNER_ID = 426L
        private const val REPORT_ID = 506L

        private val controlOverview = ControlOverview(
            startDate = LocalDate.of(2022, 12, 12),
            requestsForClarifications = "test",
            receiptOfSatisfactoryAnswers = "test answers",
            endDate = null,
            findingDescription = null,
            followUpMeasuresFromLastReport = "test from last report",
            conclusion = "result",
            followUpMeasuresForNextReport = null
        )
    }

    @MockK
    private lateinit var getReportControlOverviewCalculator: GetReportControlOverviewCalculator

    @InjectMockKs
    private lateinit var interactor: GetReportControlOverview

    @Test
    fun get() {
        every { getReportControlOverviewCalculator.get(PARTNER_ID, REPORT_ID) } returns
            controlOverview

        assertThat(interactor.get(PARTNER_ID, REPORT_ID)).isEqualTo(controlOverview)
    }

}
