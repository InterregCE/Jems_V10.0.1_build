package io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentificationCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partnerReportCreated
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class CreateProjectPartnerReport(
    private val versionPersistence: ProjectVersionPersistence,
    private val projectPersistence: ProjectPersistence,
    private val projectPartnerPersistence: PartnerPersistence,
    private val partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val projectWorkPackagePersistence: WorkPackagePersistence,
    private val projectDescriptionPersistence: ProjectDescriptionPersistence,
    private val reportPersistence: ProjectReportPersistence,
    private val reportContributionPersistence: ProjectReportContributionPersistence,
    private val currencyPersistence: CurrencyPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : CreateProjectPartnerReportInteractor {

    companion object {
        private const val MAX_REPORTS = 25
    }

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(CreateProjectPartnerReportException::class)
    override fun createReportFor(partnerId: Long): ProjectPartnerReportSummary {
        val projectId = projectPartnerPersistence.getProjectIdForPartnerId(partnerId)
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)

        val project = projectPersistence.getProject(projectId = projectId, version = version)

        validateMaxAmountOfReports(currentAmount = reportPersistence.countForPartner(partnerId = partnerId))
        validateProjectIsContracted(project)

        val report = generateReport(project = project, partnerId = partnerId, version = version)

        return reportPersistence.createPartnerReport(report).also {
            auditPublisher.publishEvent(partnerReportCreated(this, project, report, it.id))
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

    private fun generateReport(project: ProjectFull, partnerId: Long, version: String): ProjectPartnerReportCreate {
        val coFinancing = partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, version)
        return ProjectPartnerReportCreate(
            partnerId = partnerId,
            reportNumber = getLatestReportNumberIncreasedByOne(partnerId),
            status = ReportStatus.Draft,
            version = version,

            identification = projectPartnerPersistence.getById(partnerId, version).let {
                it.toReportIdentification(project).apply {
                    this.coFinancing = coFinancing.finances
                    this.currency = getCurrencyCodeForCountry(country)
                }
            },

            workPackages = projectWorkPackagePersistence
                .getWorkPackagesWithOutputsAndActivitiesByProjectId(projectId = project.id!!, version = version)
                .toCreateEntity(),

            targetGroups = projectDescriptionPersistence.getBenefits(projectId = project.id, version = version)
                ?: emptyList(),

            contributions = generateContributionsFromPreviousReports(
                partnerId = partnerId,
                partnerContributionsSorted = coFinancing.partnerContributions.sortedWith(compareBy({ it.isNotPartner() }, { it.id })),
            ),
        )
    }

    private fun getCurrencyCodeForCountry(country: String?) =
        country?.let { getCountryCodeForCountry(it) }
            ?.let { currencyPersistence.getCurrencyForCountry(it) }

    private fun getCountryCodeForCountry(country: String) =
        country.substringAfter('(').substringBefore(')')

    private fun getLatestReportNumberIncreasedByOne(partnerId: Long) =
        reportPersistence.getCurrentLatestReportNumberForPartner(partnerId).plus(1)

    private fun ProjectPartnerDetail.toReportIdentification(project: ProjectFull) = PartnerReportIdentificationCreate(
        projectIdentifier = project.customIdentifier,
        projectAcronym = project.acronym,
        partnerNumber = sortNumber!!,
        partnerAbbreviation = abbreviation,
        partnerRole = role,
        nameInOriginalLanguage = nameInOriginalLanguage,
        nameInEnglish = nameInEnglish,
        legalStatusId = legalStatusId,
        partnerType = partnerType,
        vatRecovery = vatRecovery,
        country = addresses.firstOrNull { it.type == ProjectPartnerAddressType.Organization }?.country,
        coFinancing = emptyList()
    )

    private fun List<ProjectWorkPackage>.toCreateEntity() = map { wp ->
        CreateProjectPartnerReportWorkPackage(
            workPackageId = wp.id,
            number = wp.workPackageNumber,
            activities = wp.activities.map { a ->
                CreateProjectPartnerReportWorkPackageActivity(
                    activityId = a.id,
                    number = a.activityNumber,
                    title = a.title,
                    deliverables = a.deliverables.map { d ->
                        CreateProjectPartnerReportWorkPackageActivityDeliverable(
                            deliverableId = d.id,
                            number = d.deliverableNumber,
                            title = d.title,
                        )
                    },
                )
            },
             outputs = wp.outputs.map { o ->
                 CreateProjectPartnerReportWorkPackageOutput(
                     number = o.outputNumber,
                     title = o.title,
                 )
             },
        )
    }

    private fun generateContributionsFromPreviousReports(
        partnerId: Long,
        partnerContributionsSorted: List<ProjectPartnerContribution>,
    ): List<CreateProjectPartnerReportContribution> {
        val submittedReportIds = reportPersistence.listSubmittedPartnerReports(partnerId = partnerId).mapTo(HashSet()) { it.id }

        val mapIdToHistoricalIdentifier: MutableMap<Long, UUID> = mutableMapOf()
        val contributionsNotLinkedToApplicationForm: LinkedHashMap<UUID, Pair<String?, ProjectPartnerContributionStatus?>> = LinkedHashMap()
        val historicalContributions: MutableMap<UUID, MutableList<BigDecimal>> = mutableMapOf()

        reportContributionPersistence.getAllContributionsForReportIds(reportIds = submittedReportIds).forEach {
            if (it.idFromApplicationForm != null)
                mapIdToHistoricalIdentifier[it.idFromApplicationForm] = it.historyIdentifier
            else
                contributionsNotLinkedToApplicationForm.putIfAbsent(it.historyIdentifier, it.toModel())

            historicalContributions.getOrPut(it.historyIdentifier) { mutableListOf() }
                .add(it.currentlyReported)
        }

        return partnerContributionsSorted
            .fromApplicationForm(
                idToUuid = mapIdToHistoricalIdentifier,
                historicalContributions = historicalContributions
            )
            .plus(
                contributionsNotLinkedToApplicationForm
                    .accumulatePreviousContributions(historicalContributions = historicalContributions)
            )
    }

    private fun ProjectPartnerReportEntityContribution.toModel() = Pair(sourceOfContribution, legalStatus)

    private fun List<ProjectPartnerContribution>.fromApplicationForm(
        idToUuid: Map<Long, UUID>,
        historicalContributions: Map<UUID, MutableList<BigDecimal>>,
    ) = filter { it.id != null }.map {
        (idToUuid[it.id] ?: UUID.randomUUID()).let { uuid ->
            CreateProjectPartnerReportContribution(
                sourceOfContribution = it.name,
                legalStatus = it.status?.name?.let { ProjectPartnerContributionStatus.valueOf(it) },
                idFromApplicationForm = it.id,
                historyIdentifier = uuid,
                createdInThisReport = false,
                amount = it.amount ?: BigDecimal.ZERO,
                previouslyReported = historicalContributions[uuid]?.sumOf { it } ?: BigDecimal.ZERO,
                currentlyReported = BigDecimal.ZERO,
            )
        }
    }

    private fun Map<UUID, Pair<String?, ProjectPartnerContributionStatus?>>.accumulatePreviousContributions(
        historicalContributions: Map<UUID, MutableList<BigDecimal>>,
    ) = map { (uuid, formData) ->
        CreateProjectPartnerReportContribution(
            sourceOfContribution = formData.first,
            legalStatus = formData.second,
            idFromApplicationForm = null,
            historyIdentifier = uuid,
            createdInThisReport = false,
            amount = BigDecimal.ZERO,
            previouslyReported = historicalContributions[uuid]?.sumOf { it } ?: BigDecimal.ZERO,
            currentlyReported = BigDecimal.ZERO,
        )
    }

}
