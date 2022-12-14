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
            totalEligibleAfterControl = BigDecimal.TEN,
            deletable = false,
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    lateinit var getReport: GetProjectPartnerReport

    @Test
    fun findById() {
        val report = mockk<ProjectPartnerReport>()
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 14) } returns report
        assertThat(getReport.findById(PARTNER_ID, 14L).equals(report)).isTrue
    }

    @ParameterizedTest(name = "findAll when last status {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `findAll when last status`(status: ReportStatus) {
        val lastReport = mockk<ProjectPartnerReport>()
        every { lastReport.id } returns 16L
        every { reportPersistence.getCurrentLatestReportForPartner(PARTNER_ID) } returns lastReport

        every { reportPersistence.listPartnerReports(PARTNER_ID, Pageable.unpaged()) } returns
            PageImpl(listOf(
                report(16L, status),
                report(15L, ReportStatus.Draft),
            ))
        assertThat(getReport.findAll(PARTNER_ID, Pageable.unpaged()).content).containsExactly(
            report(16L, status),
            report(15L, ReportStatus.Draft),
        )
    }

    @ParameterizedTest(name = "findAll when last is Draft {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft"])
    fun `findAll when last is Draft`(status: ReportStatus) {
        val lastReport = mockk<ProjectPartnerReport>()
        every { lastReport.id } returns 26L
        every { reportPersistence.getCurrentLatestReportForPartner(PARTNER_ID) } returns lastReport

        every { reportPersistence.listPartnerReports(PARTNER_ID, Pageable.unpaged()) } returns
            PageImpl(listOf(
                report(26L, status),
                report(25L, status),
            ))
        assertThat(getReport.findAll(PARTNER_ID, Pageable.unpaged()).content).containsExactly(
            report(26L, status).copy(deletable = true),
            report(25L, status).copy(deletable = false),
        )
    }

    @Test
    /*
     * this scenario is not very realistic, but to have it covered we should have this test
     */
    fun `findAll when there is no report`() {
        every { reportPersistence.getCurrentLatestReportForPartner(PARTNER_ID) } returns null

        every { reportPersistence.listPartnerReports(PARTNER_ID, Pageable.unpaged()) } returns
            PageImpl(listOf(
                report(36L, ReportStatus.Draft),
                report(35L, ReportStatus.Draft),
            ))
        assertThat(getReport.findAll(PARTNER_ID, Pageable.unpaged()).content).containsExactly(
            report(36L, ReportStatus.Draft).copy(deletable = false),
            report(35L, ReportStatus.Draft).copy(deletable = false),
        )
    }

}
