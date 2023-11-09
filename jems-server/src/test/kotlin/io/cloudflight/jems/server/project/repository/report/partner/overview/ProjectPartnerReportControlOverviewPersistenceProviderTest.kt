package io.cloudflight.jems.server.project.repository.report.partner.overview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.control.overview.PartnerReportControlOverviewEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistenceProvider
import io.cloudflight.jems.server.project.repository.report.partner.control.overview.ProjectPartnerReportControlOverviewRepository
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

        private val entity = PartnerReportControlOverviewEntity(
            partnerReportId = REPORT_ID,
            partnerReport = mockk(),
            startDate = LocalDate.of(2022, 5, 15),
            lastCertifiedReportIdWhenCreation = 54L,
            requestsForClarifications = "test requests",
            receiptOfSatisfactoryAnswers = "test receipt",
            endDate = LocalDate.of(2023, 11, 7),
            findingDescription = "finding desc",
            followUpMeasuresFromLastReport = "from last",
            conclusion = "conclusion test",
            followUpMeasuresForNextReport = "next report",
        )

        private val expectedEntitySaved = ControlOverview(
            startDate = LocalDate.now(),
            requestsForClarifications = null,
            receiptOfSatisfactoryAnswers = null,
            endDate = null,
            findingDescription = null,
            followUpMeasuresFromLastReport = null,
            conclusion = null,
            followUpMeasuresForNextReport = null,
            lastCertifiedReportIdWhenCreation = 22L,
        )

        private val expectedEntity = ControlOverview(
            startDate = LocalDate.of(2022, 5, 15),
            requestsForClarifications = "test requests",
            receiptOfSatisfactoryAnswers = "test receipt",
            endDate = LocalDate.of(2023, 11, 7),
            findingDescription = "finding desc",
            followUpMeasuresFromLastReport = "from last",
            conclusion = "conclusion test",
            followUpMeasuresForNextReport = "next report",
            lastCertifiedReportIdWhenCreation = 54L,
        )

    }

    @MockK
    private lateinit var controlOverviewRepository: ProjectPartnerReportControlOverviewRepository

    @MockK
    private lateinit var reportRepository: ProjectPartnerReportRepository

    @InjectMockKs
    private lateinit var persistence: ProjectPartnerReportControlOverviewPersistenceProvider

    @Test
    fun getPartnerControlReportOverview() {
        every { controlOverviewRepository.findByPartnerReportPartnerIdAndPartnerReportId(584L, REPORT_ID) } returns entity
        assertThat(persistence.getPartnerControlReportOverview(584L, REPORT_ID)).isEqualTo(expectedEntity)
    }

    @Test
    fun createPartnerControlReportOverview() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 85L
        val entityCapturingSlot = slot<PartnerReportControlOverviewEntity>()
        every { reportRepository.findByIdAndPartnerId(85L, 583L) } returns report
        every { controlOverviewRepository.save(capture(entityCapturingSlot)) } returnsArgument 0

        assertThat(persistence.createPartnerControlReportOverview(583L, 85L, 22L))
            .isEqualTo(expectedEntitySaved)

        assertThat(entityCapturingSlot.captured.partnerReportId).isEqualTo(0L) // because of OneToOne
        assertThat(entityCapturingSlot.captured.partnerReport).isEqualTo(report)
        assertThat(entityCapturingSlot.captured.startDate).isNotNull()
        assertThat(entityCapturingSlot.captured.lastCertifiedReportIdWhenCreation).isEqualTo(22L)
    }

    @Test
    fun updatePartnerControlReportOverview() {
        val entity = PartnerReportControlOverviewEntity(
            partnerReportId = REPORT_ID,
            partnerReport = mockk(),
            startDate = LocalDate.of(1992, 4, 24),
            lastCertifiedReportIdWhenCreation = 96L,
            requestsForClarifications = "test requests old",
            receiptOfSatisfactoryAnswers = "test receipt old",
            endDate = LocalDate.of(1993, 12, 27),
            findingDescription = "finding desc old",
            followUpMeasuresFromLastReport = "from last old",
            conclusion = "conclusion test old",
            followUpMeasuresForNextReport = "next report old",
        )
        every { controlOverviewRepository.findByPartnerReportPartnerIdAndPartnerReportId(582L, REPORT_ID) } returns entity

        val changes = ControlOverview(
            startDate = LocalDate.of(2056, 1, 18),
            requestsForClarifications = "req new",
            receiptOfSatisfactoryAnswers = "rec new",
            endDate = null,
            findingDescription = "finding new",
            followUpMeasuresFromLastReport = "from last new",
            conclusion = "concl new",
            followUpMeasuresForNextReport = "for next new"
        )

        persistence.updatePartnerControlReportOverview(582L, REPORT_ID, changes)

        assertThat(entity.requestsForClarifications).isEqualTo("req new")
        assertThat(entity.receiptOfSatisfactoryAnswers).isEqualTo("rec new")
        assertThat(entity.findingDescription).isEqualTo("finding new")
        assertThat(entity.followUpMeasuresFromLastReport).isEqualTo("from last new")
        assertThat(entity.followUpMeasuresForNextReport).isEqualTo("for next new")
        assertThat(entity.conclusion).isEqualTo("concl new")

        assertThat(entity.endDate).isEqualTo(LocalDate.of(1993, 12, 27)) // no change
    }

    @Test
    fun updatePartnerControlReportOverviewEndDate() {
        val entity = PartnerReportControlOverviewEntity(
            partnerReportId = REPORT_ID,
            partnerReport = mockk(),
            startDate = LocalDate.of(1992, 4, 24),
            lastCertifiedReportIdWhenCreation = 96L,
            requestsForClarifications = "test requests old",
            receiptOfSatisfactoryAnswers = "test receipt old",
            endDate = LocalDate.of(1993, 12, 27),
            findingDescription = "finding desc old",
            followUpMeasuresFromLastReport = "from last old",
            conclusion = "conclusion test old",
            followUpMeasuresForNextReport = "next report old",
        )
        every { controlOverviewRepository.findByPartnerReportPartnerIdAndPartnerReportId(581L, REPORT_ID) } returns entity

        val newEnd = LocalDate.of(2023, 12, 24)
        persistence.updatePartnerControlReportOverviewEndDate(581L, REPORT_ID, newEnd)

        // no changes
        assertThat(entity.requestsForClarifications).isEqualTo("test requests old")
        assertThat(entity.receiptOfSatisfactoryAnswers).isEqualTo("test receipt old")
        assertThat(entity.findingDescription).isEqualTo("finding desc old")
        assertThat(entity.followUpMeasuresFromLastReport).isEqualTo("from last old")
        assertThat(entity.followUpMeasuresForNextReport).isEqualTo("next report old")
        assertThat(entity.conclusion).isEqualTo("conclusion test old")

        // change only
        assertThat(entity.endDate).isEqualTo(newEnd)
    }

}
