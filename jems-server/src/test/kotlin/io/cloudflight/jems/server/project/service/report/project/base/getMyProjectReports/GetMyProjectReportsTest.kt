package io.cloudflight.jems.server.project.service.report.project.base.getMyProjectReports

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class GetMyProjectReportsTest: UnitTest() {

    companion object {
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val HOUR_AGO = ZonedDateTime.now().minusHours(1)
        private val DAYS_AGO_2 = YESTERDAY.minusDays(1).toLocalDate()

        private fun report(id: Long, status: ProjectReportStatus, projectId: Long) = ProjectReportModel(
            id = id,
            projectId = projectId,
            reportNumber = id.toInt(),
            status = status,
            startDate = DAYS_AGO_2,
            endDate = DAYS_AGO_2,
            type = ContractingDeadlineType.Both,
            linkedFormVersion = "V4.4",
            reportingDate = DAYS_AGO_2,
            createdAt = YESTERDAY,
            deadlineId = null,
            finalReport = false,
            leadPartnerNameInEnglish = "",
            leadPartnerNameInOriginalLanguage = "",
            spfPartnerId = null,
            periodNumber = id.toInt(),
            projectAcronym = "project",
            projectIdentifier = "iden-$projectId",
            firstSubmission = YESTERDAY,
            lastReSubmission = YESTERDAY,
            verificationDate = DAYS_AGO_2,
            verificationEndDate = HOUR_AGO,
            amountRequested = BigDecimal.TEN,
            totalEligibleAfterVerification = BigDecimal.ONE,
            lastVerificationReOpening = HOUR_AGO,
            riskBasedVerification = false,
            riskBasedVerificationDescription = null,
        )

        private fun reportSummary(id: Long, status: ProjectReportStatus, projectId: Long) = ProjectReportSummary(
            id = id,
            projectId = projectId,
            projectIdentifier = "iden-$projectId",
            reportNumber = id.toInt(),
            status = status,
            startDate = DAYS_AGO_2,
            endDate = DAYS_AGO_2,
            type = ContractingDeadlineType.Both,
            linkedFormVersion = "V4.4",
            firstSubmission = YESTERDAY,
            lastReSubmission = YESTERDAY,
            reportingDate = DAYS_AGO_2,
            verificationDate = DAYS_AGO_2,
            verificationEndDate = HOUR_AGO,
            createdAt = YESTERDAY,
            periodDetail = ProjectPeriod(
                number = id.toInt(),
                start = 10,
                end = 15,
            ),
            amountRequested = BigDecimal.TEN,
            totalEligibleAfterVerification = BigDecimal.ONE,
            deletable = false,
            verificationConclusionJS = null,
            verificationConclusionMA = null,
            verificationFollowup = null
        )
    }

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    private lateinit var projectCollaboratorPersistence: UserProjectCollaboratorPersistence

    @MockK
    private lateinit var securityService: SecurityService

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    private lateinit var getMyProjectReports: GetMyProjectReports

    @Test
    fun findAllOfMine() {
        val userId = 99L
        val firstProjectId = 5L
        val secondProjectId = 6L
        every { securityService.getUserIdOrThrow() } returns userId
        every { securityService.currentUser?.user?.assignedProjects } returns setOf(firstProjectId)
        every { projectCollaboratorPersistence.getProjectIdsForUser(userId) } returns setOf(secondProjectId)
        every { projectPersistence.getProjectPeriods(any(), "V4.4")} returns listOf(
            ProjectPeriod(101, 10, 15),
            ProjectPeriod(102, 10, 15))
        every { reportPersistence.listProjectReports(
            setOf(firstProjectId, secondProjectId),
            setOf(ProjectReportStatus.Submitted, ProjectReportStatus.InVerification, ProjectReportStatus.Finalized,
                ProjectReportStatus.ReOpenFinalized, ProjectReportStatus.ReOpenSubmittedLimited, ProjectReportStatus.VerificationReOpenedLimited,
            ),
            pageable = Pageable.unpaged(),
        ) } returns
            PageImpl(
                listOf(
                    report(101L, ProjectReportStatus.Submitted, firstProjectId),
                    report(102L, ProjectReportStatus.Finalized, secondProjectId),
                )
            )

        assertThat(getMyProjectReports.findAllOfMine(Pageable.unpaged())).containsExactly(
            reportSummary(101L, ProjectReportStatus.Submitted, firstProjectId),
            reportSummary(102L, ProjectReportStatus.Finalized, secondProjectId),
        )
    }
}
