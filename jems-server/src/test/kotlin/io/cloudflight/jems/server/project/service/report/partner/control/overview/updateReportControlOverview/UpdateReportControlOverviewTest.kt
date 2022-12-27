package io.cloudflight.jems.server.project.service.report.partner.control.overview.updateReportControlOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.cloudflight.jems.server.project.service.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class UpdateReportControlOverviewTest: UnitTest() {
    companion object {
        private const val PARTNER_ID = 426L
        private const val REPORT_ID = 506L

        private val controlOverview = ControlOverview(
            startDate = LocalDate.now(),
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
    private lateinit var projectPartnerReportControlOverviewPersistence: ProjectPartnerReportControlOverviewPersistence

    @InjectMockKs
    private lateinit var interactor: UpdateReportControlOverview

    @Test
    fun update() {
        every { projectPartnerReportControlOverviewPersistence.updatePartnerControlReportOverview(
            PARTNER_ID, REPORT_ID, controlOverview
        ) } returns controlOverview
        every { interactor.update(PARTNER_ID, REPORT_ID, controlOverview) } returns controlOverview
    }
}
