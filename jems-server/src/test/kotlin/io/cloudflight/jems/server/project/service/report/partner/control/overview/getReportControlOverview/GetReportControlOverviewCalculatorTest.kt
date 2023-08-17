package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class GetReportControlOverviewCalculatorTest: UnitTest() {

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

        private val finalizedControlOverview = ControlOverview(
            startDate = LocalDate.of(2022, 12, 12),
            requestsForClarifications = "test",
            receiptOfSatisfactoryAnswers = "test answers",
            endDate = LocalDate.of(2022, 12, 15),
            findingDescription = "",
            followUpMeasuresFromLastReport = "test from last report",
            conclusion = "result",
            followUpMeasuresForNextReport = "for next report"
        )

        private val lastCertifiedReport = ProjectPartnerReport(
            id = REPORT_ID,
            reportNumber = 2,
            status = ReportStatus.Certified,
            version = "v1.0",
            firstSubmission = mockk(),
            lastResubmission = null,
            controlEnd = null,
            lastControlReopening = null,
            projectReportId = 24L,
            projectReportNumber = 240,
            identification = mockk(),
        )

        private val lastCertifiedControlOverview = ControlOverview(
            startDate = LocalDate.of(2022, 12, 8),
            requestsForClarifications = "test",
            receiptOfSatisfactoryAnswers = "test answers",
            endDate = LocalDate.of(2022, 12, 10),
            findingDescription = null,
            followUpMeasuresFromLastReport = "test from last report",
            conclusion = "result",
            followUpMeasuresForNextReport = "test follow up measures for next report"
        )

        private val controlOverviewAfterLastCertified = ControlOverview(
            startDate = LocalDate.of(2022, 12, 12),
            requestsForClarifications = "test",
            receiptOfSatisfactoryAnswers = "test answers",
            endDate = null,
            findingDescription = null,
            followUpMeasuresFromLastReport = "test from last report",
            conclusion = "result",
            followUpMeasuresForNextReport = null,
            previousFollowUpMeasuresFromLastReport = "test follow up measures for next report",
            lastCertifiedReportNumber = 10,
            lastCertifiedReportIdWhenCreation = 9,
            changedLastCertifiedReportEndDate = LocalDate.of(2022, 12, 10)
        )
    }

    @MockK
    private lateinit var controlOverviewPersistence: ProjectPartnerReportControlOverviewPersistence
    @MockK
    private lateinit var projectPartnerReportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    private lateinit var interactor: GetReportControlOverviewCalculator

    @Test
    fun get() {
        every { controlOverviewPersistence.getPartnerControlReportOverview(PARTNER_ID, REPORT_ID) } returns
                controlOverview
        every { projectPartnerReportPersistence.getLastCertifiedPartnerReportId(PARTNER_ID) } returns null
        every { projectPartnerReportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, REPORT_ID) } returns
                ProjectPartnerReportStatusAndVersion(REPORT_ID, ReportStatus.InControl, "v1.0")

        Assertions.assertThat(interactor.get(PARTNER_ID, REPORT_ID)).isEqualTo(controlOverview)
    }

    @Test
    fun getWithLastCertifiedReport() {
        every { controlOverviewPersistence.getPartnerControlReportOverview(PARTNER_ID, REPORT_ID) } returns
                controlOverviewAfterLastCertified
        every { projectPartnerReportPersistence.getLastCertifiedPartnerReportId(PARTNER_ID) } returns 10L
        every { projectPartnerReportPersistence.getPartnerReportById(PARTNER_ID, 10L) } returns lastCertifiedReport
        every { controlOverviewPersistence.getPartnerControlReportOverview(PARTNER_ID, 10L) } returns lastCertifiedControlOverview
        every { projectPartnerReportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, REPORT_ID) } returns
                ProjectPartnerReportStatusAndVersion(REPORT_ID, ReportStatus.InControl, "v1.0")

        Assertions.assertThat(interactor.get(PARTNER_ID, REPORT_ID)).isEqualTo(controlOverviewAfterLastCertified)
    }

    @Test
    fun getFinalizedReport() {
        every { controlOverviewPersistence.getPartnerControlReportOverview(PARTNER_ID, REPORT_ID) } returns
                finalizedControlOverview
        every { projectPartnerReportPersistence.getLastCertifiedPartnerReportId(PARTNER_ID) } returns null
        every { projectPartnerReportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, REPORT_ID) } returns
                ProjectPartnerReportStatusAndVersion(REPORT_ID, ReportStatus.Certified, "v1.0")

        Assertions.assertThat(interactor.get(PARTNER_ID, REPORT_ID)).isEqualTo(finalizedControlOverview)
    }
}
