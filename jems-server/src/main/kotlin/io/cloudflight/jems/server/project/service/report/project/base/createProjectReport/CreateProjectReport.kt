package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReportNotSpecific
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceModel
import io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport.UpdateProjectReport.Companion.validateInput
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportCreated
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.ZonedDateTime

@Service
class CreateProjectReport(
    private val versionPersistence: ProjectVersionPersistence,
    private val projectPersistence: ProjectPersistence,
    private val projectPartnerPersistence: PartnerPersistence,
    private val reportPersistence: ProjectReportPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectDescriptionPersistence: ProjectDescriptionPersistence,
    private val projectReportIdentificationPersistence: ProjectReportIdentificationPersistence,
) : CreateProjectReportInteractor {

    companion object {
        private const val MAX_REPORTS = 25
    }

    @CanEditProjectReportNotSpecific
    @Transactional
    @ExceptionWrapper(CreateProjectReportException::class)
    override fun createReportFor(projectId: Long, data: ProjectReportUpdate): ProjectReport {
        validateMaxAmountOfReports(currentAmount = reportPersistence.countForProject(projectId = projectId))

        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
        val project = projectPersistence.getProject(projectId = projectId, version = version)
        validateProjectIsContracted(project)

        val periods = projectPersistence.getProjectPeriods(projectId, version).associateBy { it.number }
        data.validateInput(validPeriodNumbers = periods.keys,
            datesInvalidExceptionResolver = { StartDateIsAfterEndDate() },
            linkToDeadlineWithManualDataExceptionResolver = { LinkToDeadlineProvidedWithManualDataOverride() },
            noLinkAndDataMissingExceptionResolver = { LinkToDeadlineNotProvidedAndDataMissing() },
            periodNumberExceptionResolver = { PeriodNumberInvalid(it) },
        )

        val latestReportNumber = reportPersistence.getCurrentLatestReportFor(projectId)?.reportNumber ?: 0
        val partners = projectPartnerPersistence.findTop30ByProjectId(projectId, version).toSet()
        val leadPartner = partners.firstOrNull { it.role == ProjectPartnerRole.LEAD_PARTNER }

        val targetGroups = projectDescriptionPersistence.getBenefits(projectId = projectId, version = version) ?: emptyList()

        return reportPersistence.createReportAndFillItToEmptyCertificates(
            report = data.toCreateModel(latestReportNumber, version, project, leadPartner),
            targetGroups = targetGroups,
            previouslyReportedByPartner = getPreviouslyReportedByPartners(projectId, partners)
        ).also {
            auditPublisher.publishEvent(projectReportCreated(this, project, it))
        }.toServiceModel({ periodNumber -> periods[periodNumber]!! })
    }


    private fun getPreviouslyReportedByPartners(projectId: Long, partners: Set<ProjectPartnerDetail>): Map<Long, BigDecimal> {
        val submittedReportIds = reportPersistence.getSubmittedProjectReportIds(projectId)
        val cumulative = projectReportIdentificationPersistence.getSpendingProfileCumulative(submittedReportIds)
        return partners.associate { Pair(it.id, cumulative.getOrDefault(it.id, BigDecimal.ZERO)) }
    }

    private fun validateMaxAmountOfReports(currentAmount: Int) {
        if (currentAmount >= MAX_REPORTS)
            throw MaxAmountOfReportsReachedException()
    }

    private fun validateProjectIsContracted(project: ProjectFull) {
        if (!project.projectStatus.status.isAlreadyContracted())
            throw ReportCanBeCreatedOnlyWhenContractedException()
    }

    private fun ProjectReportUpdate.toCreateModel(
        latestReportNumber: Int,
        version: String,
        project: ProjectFull,
        leadPartner: ProjectPartnerDetail?,
    ) = ProjectReportModel(
        reportNumber = latestReportNumber.plus(1),
        status = ProjectReportStatus.Draft,
        linkedFormVersion = version,
        startDate = startDate,
        endDate = endDate,
        deadlineId = deadlineId,
        type = type,
        periodNumber = periodNumber,
        reportingDate = reportingDate,
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
