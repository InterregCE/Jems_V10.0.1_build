package io.cloudflight.jems.server.project.service.report.project.base.getProjectReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetProjectReportTest : UnitTest() {

    companion object {
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)
        private val MONTH_AGO = LocalDate.now().minusMonths(1)
        private val NOW = ZonedDateTime.now()
        private val WEEK_AGO = ZonedDateTime.now().minusWeeks(1)
        private val NEXT_MONTH = ZonedDateTime.now().plusMonths(1)

        val report = ProjectReportModel(
            id = 14L,
            reportNumber = 4,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "v4",
            startDate = YESTERDAY,
            endDate = TOMORROW,
            deadlineId = 14L,
            type = ContractingDeadlineType.Content,
            periodNumber = 7,
            reportingDate = MONTH_AGO,
            projectId = 114L,
            projectIdentifier = "proj identifier",
            projectAcronym = "project acronym",
            leadPartnerNameInOriginalLanguage = "LP orig",
            leadPartnerNameInEnglish = "LP english",
            createdAt = NOW,
            firstSubmission = WEEK_AGO,
            verificationDate = NEXT_MONTH.toLocalDate(),
            verificationEndDate = NEXT_MONTH,
            amountRequested = BigDecimal.valueOf(15L),
            totalEligibleAfterVerification = BigDecimal.valueOf(19L),
            riskBasedVerification = false,
            riskBasedVerificationDescription = "Description"
        )

        val period7 = ProjectPeriod(7, 13, 14)
        val period8 = ProjectPeriod(8, 15, 16)

        val expectedReport = ProjectReport(
            id = 14L,
            reportNumber = 4,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "v4",
            startDate = YESTERDAY,
            endDate = TOMORROW,
            deadlineId = 14L,
            type = ContractingDeadlineType.Content,
            periodDetail = period7,
            reportingDate = MONTH_AGO,
            projectId = 114L,
            projectIdentifier = "proj identifier",
            projectAcronym = "project acronym",
            leadPartnerNameInOriginalLanguage = "LP orig",
            leadPartnerNameInEnglish = "LP english",
            createdAt = NOW,
            firstSubmission = WEEK_AGO,
            verificationDate = NEXT_MONTH.toLocalDate(),
            verificationEndDate = NEXT_MONTH
        )

        val expectedReportSummary = ProjectReportSummary(
            id = 14L,
            projectId = 114L,
            projectIdentifier = "proj identifier",
            reportNumber = 4,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "v4",
            startDate = YESTERDAY,
            endDate = TOMORROW,
            type = ContractingDeadlineType.Content,
            periodDetail = period7,
            reportingDate = MONTH_AGO,
            createdAt = NOW,
            firstSubmission = WEEK_AGO,
            verificationDate = NEXT_MONTH.toLocalDate(),
            deletable = false,
            verificationEndDate = NEXT_MONTH,
            amountRequested = BigDecimal.valueOf(15L),
            totalEligibleAfterVerification = BigDecimal.valueOf(19L),
            verificationConclusionJS = null,
            verificationConclusionMA = null,
            verificationFollowup = null,
        )

    }

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, projectPersistence)
    }

    @Test
    fun findById() {
        val projectId = 114L
        every { reportPersistence.getReportById(projectId, reportId = 14L) } returns report
        every { projectPersistence.getProjectPeriods(projectId, "v4") } returns listOf(period7)
        assertThat(interactor.findById(projectId, reportId = 14L)).isEqualTo(expectedReport)
    }

}
