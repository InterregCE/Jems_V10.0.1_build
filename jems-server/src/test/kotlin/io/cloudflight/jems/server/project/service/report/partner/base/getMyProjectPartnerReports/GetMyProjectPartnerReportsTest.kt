package io.cloudflight.jems.server.project.service.report.partner.base.getMyProjectPartnerReports

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.base.getProjectPartnerReport.GetProjectPartnerReport
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class GetMyProjectPartnerReportsTest: UnitTest() {

    companion object {
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val HOUR_AGO = ZonedDateTime.now().minusHours(1)
        private val DAYS_AGO_2 = YESTERDAY.minusDays(1)

        private fun report(id: Long, status: ReportStatus, projectId: Long, partnerId: Long) = ProjectPartnerReportSummary(
            id = id,
            projectId = projectId,
            partnerId = partnerId,
            projectCustomIdentifier = "dummy",
            partnerRole = ProjectPartnerRole.PARTNER,
            partnerNumber = 1,
            partnerAbbreviation = "partner A",
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

    @MockK
    private lateinit var projectCollaboratorPersistence: UserProjectCollaboratorPersistence

    @MockK
    private lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    private lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @MockK
    private lateinit var partnerPersistence: PartnerPersistence

    @MockK
    private lateinit var securityService: SecurityService

    @InjectMockKs
    private lateinit var getMyProjectPartnerReports: GetMyProjectPartnerReports

    @Test
    fun findAllOfMine() {
        val userId = 99L
        val firstProjectId = 5L
        val secondProjectId = 6L
        val firstPartnerId = 44L
        val secondPartnerId = 88L
        every { securityService.getUserIdOrThrow() } returns userId
        every { partnerCollaboratorPersistence.findPartnersByUser(userId) } returns setOf(firstPartnerId)
        every { controllerInstitutionPersistence.getRelatedProjectAndPartnerIdsForUser(userId) } returns mapOf(firstProjectId to setOf(secondPartnerId))
        every { securityService.currentUser?.user?.assignedProjects } returns setOf(firstProjectId)
        every { projectCollaboratorPersistence.getProjectIdsForUser(userId) } returns setOf(secondProjectId)
        every { partnerPersistence.getPartnerIdsByProjectIds(setOf(firstProjectId, secondProjectId)) } returns setOf(firstPartnerId)
        every { reportPersistence.listPartnerReports(
            setOf(firstPartnerId, secondPartnerId),
            ReportStatus.FINANCIALLY_CLOSED_STATUSES,
            Pageable.unpaged()
        ) } returns
            PageImpl(
                listOf(
                    report(101L, ReportStatus.Submitted, firstProjectId, firstPartnerId),
                    report(102L, ReportStatus.Certified, secondProjectId, secondPartnerId)
                )
            )

        assertThat(getMyProjectPartnerReports.findAllOfMine(Pageable.unpaged()).content).containsExactly(
            report(101L, ReportStatus.Submitted, firstProjectId, firstPartnerId),
            report(102L, ReportStatus.Certified, secondProjectId, secondPartnerId)
        )
    }
}
