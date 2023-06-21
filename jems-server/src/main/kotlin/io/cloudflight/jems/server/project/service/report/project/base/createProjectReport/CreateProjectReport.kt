package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReportNotSpecific
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.PartnerReportInvestmentSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportCreateModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportPartnerCreateModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportResultCreate
import io.cloudflight.jems.server.project.service.report.partner.base.createProjectPartnerReport.toCreateEntity
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportCreatePersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceModel
import io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport.UpdateProjectReport.Companion.validateInput
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportCreated
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
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
    private val reportCreatePersistence: ProjectReportCreatePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectWorkPackagePersistence: WorkPackagePersistence,
    private val projectDescriptionPersistence: ProjectDescriptionPersistence,
    private val projectReportIdentificationPersistence: ProjectReportIdentificationPersistence,
    private val createProjectReportBudget: CreateProjectReportBudget,
    private val projectResultPersistence: ProjectResultPersistence,
    private val projectReportResultPersistence: ProjectReportResultPrinciplePersistence,
    private val workPlanPersistence: ProjectReportWorkPlanPersistence,
) : CreateProjectReportInteractor {

    companion object {
        private const val MAX_REPORTS = 25

        private val emptyPrinciples = ProjectHorizontalPrinciples(null, null, null)
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
        val partners = projectPartnerPersistence.findTop50ByProjectId(projectId, version).toSet()
        val leadPartner = partners.firstOrNull { it.role == ProjectPartnerRole.LEAD_PARTNER }
        val submittedReports = reportPersistence.getSubmittedProjectReportIds(projectId)
        val submittedReportIds = submittedReports.mapTo(HashSet()) { it.first }

        val workPackages = projectWorkPackagePersistence
            .getWorkPackagesWithAllDataByProjectId(projectId = project.id!!, version = version)

        val targetGroups = projectDescriptionPersistence.getBenefits(projectId = projectId, version = version) ?: emptyList()

        // Project Results & Horizontal Principles
        val projectResults = projectResultPersistence
            .getResultsForProject(projectId = projectId, version = version)
            .toCreateModel(previouslyReportedByNumber = projectReportResultPersistence.getResultCumulative(submittedReportIds))
        val projectManagement = projectDescriptionPersistence.getProjectManagement(projectId = projectId, version = version)

        val lastSubmittedReportIdWithWorkPlan = submittedReports.firstOrNull { it.second.hasContent() }?.first
        val reportToCreate = ProjectReportCreateModel(
            reportBase = data.toCreateModel(latestReportNumber, version, project, leadPartner),
            reportBudget = createProjectReportBudget.retrieveBudgetDataFor(
                projectId = projectId,
                version = version,
                investments = workPackages.extractInvestments()
            ),
            workPackages = workPackages.toCreateEntity(
                previouslyReportedDeliverables = workPlanPersistence.getDeliverableCumulative(submittedReportIds),
                previouslyReportedOutputs = workPlanPersistence.getOutputCumulative(submittedReportIds),
                lastWorkPlan = lastSubmittedReportIdWithWorkPlan?.let { workPlanPersistence.getReportWorkPlanById(projectId, it) } ?: emptyList(),
            ),
            targetGroups = targetGroups,
            partners = getPreviouslyReportedByPartner(submittedReportIds, partners),
            results = projectResults,
            horizontalPrinciples = projectManagement?.projectHorizontalPrinciples ?: emptyPrinciples
        )

        return reportCreatePersistence.createReportAndFillItToEmptyCertificates(reportToCreate).also {
            auditPublisher.publishEvent(projectReportCreated(this, project, it))
        }.toServiceModel { periodNumber -> periods[periodNumber]!! }
    }

    private fun getPreviouslyReportedByPartner(
        submittedReportIds: Set<Long>,
        partners: Collection<ProjectPartnerDetail>
    ): List<ProjectReportPartnerCreateModel> {
        val previouslyReported = projectReportIdentificationPersistence.getSpendingProfileCumulative(submittedReportIds)
        return partners.map {
            ProjectReportPartnerCreateModel(
                partnerId = it.id,
                partnerNumber = it.sortNumber!!,
                partnerAbbreviation = it.abbreviation,
                partnerRole = it.role,
                country = it.addresses.firstOrNull { it.type == ProjectPartnerAddressType.Organization }?.country,
                previouslyReported = previouslyReported[it.id] ?: BigDecimal.ZERO,
            )
        }
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

    private fun List<ProjectResult>.toCreateModel(
        previouslyReportedByNumber: Map<Int, BigDecimal>,
    ): List<ProjectReportResultCreate> = map {
        ProjectReportResultCreate(
            resultNumber = it.resultNumber,
            deactivated = it.deactivated,
            periodNumber = it.periodNumber,
            programmeResultIndicatorId = it.programmeResultIndicatorId,
            baseline = it.baseline,
            targetValue = it.targetValue ?: BigDecimal.ZERO,
            previouslyReported = previouslyReportedByNumber.getOrDefault(it.resultNumber, BigDecimal.ZERO),
        )
    }

    private fun List<ProjectWorkPackageFull>.extractInvestments() = map { wp ->
        wp.investments.map {
            PartnerReportInvestmentSummary(
                investmentId = it.id!!,
                investmentNumber = it.investmentNumber,
                workPackageNumber = wp.workPackageNumber,
                title = it.title,
                deactivated = it.deactivated,
            )
        }
    }.flatten()

}
