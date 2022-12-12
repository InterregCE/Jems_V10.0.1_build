package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReportNotSpecific
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceModel
import io.cloudflight.jems.server.project.service.report.project.projectReportCreated
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class CreateProjectReport(
    private val versionPersistence: ProjectVersionPersistence,
    private val projectPersistence: ProjectPersistence,
    private val projectPartnerPersistence: PartnerPersistence,
    private val reportPersistence: ProjectReportPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : CreateProjectReportInteractor {

    companion object {
        private const val MAX_REPORTS = 25
        private val emptyPeriod = ProjectPeriod(0, 0, 0)
    }

    @CanEditProjectReportNotSpecific
    @Transactional
    @ExceptionWrapper(CreateProjectReportException::class)
    override fun createReportFor(projectId: Long): ProjectReport {
        validateMaxAmountOfReports(currentAmount = reportPersistence.countForProject(projectId = projectId))

        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
        val project = projectPersistence.getProject(projectId = projectId, version = version)
        validateProjectIsContracted(project)

        val latestReportNumber = reportPersistence.getCurrentLatestReportFor(projectId)?.reportNumber ?: 0

        val leadPartner = projectPartnerPersistence
            .findTop30ByProjectId(projectId, version)
            .firstOrNull { it.role == ProjectPartnerRole.LEAD_PARTNER }

        // TODO MP2-3011 type, period and date from schedule if exists
        val periodFromSchedule = ProjectReportDeadline(null, null, null, null)

        return reportPersistence.createReport(
            toCreateModel(latestReportNumber, version, project, leadPartner, periodFromSchedule)
        ).also {
            auditPublisher.publishEvent(projectReportCreated(this, project, it))
        }.toServiceModel({ emptyPeriod })
    }

    private fun validateMaxAmountOfReports(currentAmount: Int) {
        if (currentAmount >= MAX_REPORTS)
            throw MaxAmountOfReportsReachedException()
    }

    private fun validateProjectIsContracted(project: ProjectFull) {
        if (!project.projectStatus.status.isAlreadyContracted())
            throw ReportCanBeCreatedOnlyWhenContractedException()
    }

    private fun toCreateModel(
        latestReportNumber: Int,
        version: String,
        project: ProjectFull,
        leadPartner: ProjectPartnerDetail?,
        deadlineData: ProjectReportDeadline,
    ) = ProjectReportModel(
        reportNumber = latestReportNumber.plus(1),
        status = ProjectReportStatus.Draft,
        linkedFormVersion = version,
        startDate = null,
        endDate = null,
        deadlineId = deadlineData.deadlineId,
        type = deadlineData.type,
        periodNumber = deadlineData.periodNumber,
        reportingDate = deadlineData.reportingDate,
        projectId = project.id!!,
        projectIdentifier = project.customIdentifier,
        projectAcronym = project.acronym,
        leadPartnerNameInOriginalLanguage = leadPartner?.nameInOriginalLanguage ?: "",
        leadPartnerNameInEnglish = leadPartner?.nameInEnglish ?: "",
        createdAt = ZonedDateTime.now(),
        firstSubmission = null,
        verificationDate = null,
    )

}
