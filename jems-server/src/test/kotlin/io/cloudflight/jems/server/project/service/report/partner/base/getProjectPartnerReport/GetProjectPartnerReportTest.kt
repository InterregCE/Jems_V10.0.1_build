package io.cloudflight.jems.server.project.service.report.partner.base.getProjectPartnerReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class GetProjectPartnerReportTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 588L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val HOUR_AGO = ZonedDateTime.now().minusHours(1)
        private val DAYS_AGO_2 = YESTERDAY.minusDays(1)

        private fun report(id: Long, status: ReportStatus) = ProjectPartnerReportSummary(
            id = id,
            reportNumber = id.toInt(),
            status = status,
            version = "V4.4",
            firstSubmission = YESTERDAY,
            lastReSubmission = DAYS_AGO_2,
            controlEnd = HOUR_AGO,
            createdAt = DAYS_AGO_2,
            startDate = null,
            endDate = null,
            periodDetail = ProjectPartnerReportPeriod(
                number = id.toInt(),
                periodBudget = BigDecimal.ONE,
                periodBudgetCumulative = BigDecimal.TEN,
                start = 10,
                end = 15,
            ),
            projectReportId = 648L,
            projectReportNumber = 649,
            totalEligibleAfterControl = BigDecimal.TEN,
            totalAfterSubmitted = BigDecimal.ONE,
            deletable = false,
        )
    }

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    private lateinit var getReport: GetProjectPartnerReport

    @Test
    fun findById() {
        val report = mockk<ProjectPartnerReport>()
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 14) } returns report
        assertThat(getReport.findById(PARTNER_ID, 14L).equals(report)).isTrue
    }

    @ParameterizedTest(name = "findAll when last status Draft and isDeletable {0}")
    @ValueSource(booleans = [true, false])
    fun `findAll when last status Draft and isDeletable`(isDeletable: Boolean) {
        val lastReport = mockk<ProjectPartnerReport>()
        every { lastReport.id } returns if (isDeletable) 10L else 9457L
        every { reportPersistence.getCurrentLatestReportForPartner(PARTNER_ID) } returns lastReport

        every { reportPersistence.listPartnerReports(PARTNER_ID, Pageable.unpaged()) } returns
            PageImpl(ReportStatus.values().map { report(10L + it.ordinal, it) })

        assertThat(getReport.findAll(PARTNER_ID, Pageable.unpaged()).content).containsExactly(
            report(10L, ReportStatus.Draft).copy(deletable = isDeletable, totalEligibleAfterControl = null, totalAfterSubmitted = null),
            report(11L, ReportStatus.Submitted).copy(totalEligibleAfterControl = null),
            report(12L, ReportStatus.ReOpenSubmittedLast).copy(totalEligibleAfterControl = null, totalAfterSubmitted = null),
            report(13L, ReportStatus.ReOpenSubmittedLimited).copy(totalEligibleAfterControl = null),
            report(14L, ReportStatus.InControl),
            report(15L, ReportStatus.ReOpenInControlLast).copy(totalAfterSubmitted = null),
            report(16L, ReportStatus.ReOpenInControlLimited),
            report(17L, ReportStatus.Certified),
        )
    }

    @ParameterizedTest(name = "findAll when last status is not draft, but {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `findAll when last status is not draft`(status: ReportStatus) {
        val lastReport = mockk<ProjectPartnerReport>()
        every { lastReport.id } returns 20L
        every { reportPersistence.getCurrentLatestReportForPartner(PARTNER_ID) } returns lastReport

        every { reportPersistence.listPartnerReports(PARTNER_ID, Pageable.unpaged()) } returns
            PageImpl(
                listOf(report(20L, status))
                    .plus(ReportStatus.values().map { report(21L + it.ordinal, it) }) // should not affect test
            )

        assertThat(getReport.findAll(PARTNER_ID, Pageable.unpaged()).content).containsExactly(
            report(20L, status).copy(
                deletable = false,
                // test removal of total when status is not yet in control
                totalEligibleAfterControl = if (status in setOf(
                        ReportStatus.InControl,
                        ReportStatus.ReOpenInControlLast,
                        ReportStatus.ReOpenInControlLimited,
                        ReportStatus.Certified,
                )) BigDecimal.TEN else null,
                totalAfterSubmitted = if (status in setOf(
                    ReportStatus.Draft,
                    ReportStatus.ReOpenSubmittedLast,
                    ReportStatus.ReOpenInControlLast,
                )) null else BigDecimal.ONE,
            ),
            report(21L, ReportStatus.Draft).copy(deletable = false, totalEligibleAfterControl = null, totalAfterSubmitted = null),
            report(22L, ReportStatus.Submitted).copy(totalEligibleAfterControl = null),
            report(23L, ReportStatus.ReOpenSubmittedLast).copy(totalEligibleAfterControl = null, totalAfterSubmitted = null),
            report(24L, ReportStatus.ReOpenSubmittedLimited).copy(totalEligibleAfterControl = null),
            report(25L, ReportStatus.InControl),
            report(26L, ReportStatus.ReOpenInControlLast).copy(totalAfterSubmitted = null),
            report(27L, ReportStatus.ReOpenInControlLimited),
            report(28L, ReportStatus.Certified),
        )
    }

}
