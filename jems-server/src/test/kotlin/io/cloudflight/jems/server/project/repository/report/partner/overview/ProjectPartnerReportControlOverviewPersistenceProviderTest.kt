package io.cloudflight.jems.server.project.repository.report.partner.overview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.control.overview.PartnerReportControlOverviewEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistenceProvider
import io.cloudflight.jems.server.project.repository.report.partner.control.overview.ProjectPartnerReportControlOverviewRepository
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ProjectPartnerReportControlOverviewPersistenceProviderTest: UnitTest() {

    companion object {
        private const val REPORT_ID = 256L
        private const val PARTNER_ID = 581L

        private val test = ProjectPartnerReportEntity(
            id = REPORT_ID,
            partnerId = PARTNER_ID,
            number = 1,
            status = ReportStatus.InControl,
            applicationFormVersion = "v1.0",
            identification = mockk(),
            controlEnd = null,
            firstSubmission = mockk(),
            lastReSubmission = mockk(),
            projectReport = mockk(),
            lastControlReopening = null
        )

        private val entity = PartnerReportControlOverviewEntity(
            partnerReport = test,
            startDate = LocalDate.of(2022, 5, 15),
            findingDescription = "test",
            requestsForClarifications = "test requests"
        )

        private val entitySaved = PartnerReportControlOverviewEntity(
            partnerReport = test,
            startDate = LocalDate.of(2022, 5, 15),
            findingDescription = "test",
            requestsForClarifications = "test requests"
        )

        private val controlOverview = ControlOverview(
            startDate = LocalDate.of(2022, 5, 15),
            requestsForClarifications = "test requests",
            receiptOfSatisfactoryAnswers = null,
            endDate = null,
            findingDescription = "test",
            followUpMeasuresFromLastReport = null,
            conclusion = null,
            followUpMeasuresForNextReport = null
        )

        private val entityWithEndDate = PartnerReportControlOverviewEntity(
            partnerReport = test,
            startDate = LocalDate.of(2022, 5, 15),
            findingDescription = "test",
            requestsForClarifications = "test requests",
            endDate = LocalDate.of(2022, 5, 25)
        )

        private val controlOverviewWithEndDate = ControlOverview(
            startDate = LocalDate.of(2022, 5, 15),
            requestsForClarifications = "test requests",
            receiptOfSatisfactoryAnswers = null,
            endDate = LocalDate.of(2022, 5, 25),
            findingDescription = "test",
            followUpMeasuresFromLastReport = null,
            conclusion = null,
            followUpMeasuresForNextReport = null
        )
    }

    @MockK
    private lateinit var controlOverviewRepository: ProjectPartnerReportControlOverviewRepository

    @MockK
    private lateinit var reportRepository: ProjectPartnerReportRepository

    @InjectMockKs
    private lateinit var persistence: ProjectPartnerReportControlOverviewPersistenceProvider

    @Test
    fun `get report control overview`() {
        every { controlOverviewRepository.findByPartnerReportPartnerIdAndPartnerReportId(PARTNER_ID, REPORT_ID) } returns
            entity
        assertThat(persistence.getPartnerControlReportOverview(PARTNER_ID, REPORT_ID)).isEqualTo(controlOverview)
    }

    @Test
    fun `create report control overview`() {
        val entityCapturingSlot = slot<PartnerReportControlOverviewEntity>()
        every { reportRepository.findByIdAndPartnerId(REPORT_ID, PARTNER_ID) } returns test
        every { controlOverviewRepository.save(capture(entityCapturingSlot)) } returns entitySaved
        val savedControlOverview = persistence.createPartnerControlReportOverview(PARTNER_ID, REPORT_ID, null)

        assertThat(savedControlOverview).isNotNull
        assertThat(savedControlOverview).isEqualTo(controlOverview)
    }

    @Test
    fun `update report control overview`() {
        val entityCapturingSlot = slot<PartnerReportControlOverviewEntity>()
        every { reportRepository.findByIdAndPartnerId(REPORT_ID, PARTNER_ID) } returns test
        every { controlOverviewRepository.save(capture(entityCapturingSlot)) } returns entitySaved
        val savedControlOverview = persistence.updatePartnerControlReportOverview(PARTNER_ID, REPORT_ID, controlOverview)

        assertThat(savedControlOverview).isNotNull
        assertThat(savedControlOverview).isEqualTo(controlOverview)
    }

    @Test
    fun `update report control overview end date`() {
        val entityCapturingSlot = slot<PartnerReportControlOverviewEntity>()
        every { reportRepository.findByIdAndPartnerId(REPORT_ID, PARTNER_ID) } returns test
        every { controlOverviewRepository.save(capture(entityCapturingSlot)) } returns entityWithEndDate
        every { controlOverviewRepository.findByPartnerReportPartnerIdAndPartnerReportId(PARTNER_ID, REPORT_ID) } returns entitySaved
        val savedControlOverview = persistence.updatePartnerControlReportOverviewEndDate(PARTNER_ID, REPORT_ID, LocalDate.of(2022, 5, 15))

        assertThat(savedControlOverview).isNotNull
        assertThat(savedControlOverview).isEqualTo(controlOverviewWithEndDate)
    }
}
