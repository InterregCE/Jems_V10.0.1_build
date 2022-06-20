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
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.report.ProjectReportCreatePersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportIdentificationCreate
import io.cloudflight.jems.server.project.service.report.model.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportBaseData
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.partnerReportCreated
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProjectPartnerReport(
    private val versionPersistence: ProjectVersionPersistence,
    private val projectPersistence: ProjectPersistence,
    private val projectPartnerPersistence: PartnerPersistence,
    private val partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val projectWorkPackagePersistence: WorkPackagePersistence,
    private val projectDescriptionPersistence: ProjectDescriptionPersistence,
    private val reportPersistence: ProjectReportPersistence,
    private val reportCreatePersistence: ProjectReportCreatePersistence,
    private val currencyPersistence: CurrencyPersistence,
    private val createProjectPartnerReportBudget: CreateProjectPartnerReportBudget,
    private val auditPublisher: ApplicationEventPublisher,
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

        val baseData = generateReportBaseData(
            partnerId = partnerId,
            latestReportNumber = reportPersistence.getCurrentLatestReportNumberForPartner(partnerId),
            version = version,
        )

        val coFinancing = partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, version)
        val partner = projectPartnerPersistence.getById(partnerId, version)
        val identification = generateReportIdentification(
            partner = partner,
            project = project,
            finances = coFinancing.finances,
            currencyResolver = { currencyPersistence.getCurrencyForCountry(it) },
        )

        val workPackages = projectWorkPackagePersistence
            .getWorkPackagesWithOutputsAndActivitiesByProjectId(projectId = project.id!!, version = version)
            .toCreateEntity()

        val budget = createProjectPartnerReportBudget.retrieveBudgetDataFor(
            projectId = projectId,
            partner = partner.toSummary(),
            version = version,
            partnerContributions = coFinancing.partnerContributions,
        )

        val report = ProjectPartnerReportCreate(
            baseData = baseData,
            identification = identification,
            workPackages = workPackages,
            targetGroups = projectDescriptionPersistence.getBenefits(projectId = project.id, version = version) ?: emptyList(),
            budget = budget,
        )

        return reportCreatePersistence.createPartnerReport(report).also {
            auditPublisher.publishEvent(partnerReportCreated(this, project, report, it.id))
        }
    }

    private fun generateReportBaseData(
        partnerId: Long,
        latestReportNumber: Int,
        version: String,
    ): PartnerReportBaseData {
        return PartnerReportBaseData(
            partnerId = partnerId,
            reportNumber = latestReportNumber.plus(1),
            status = ReportStatus.Draft,
            version = version,
        )
    }

    private fun generateReportIdentification(
        partner: ProjectPartnerDetail,
        project: ProjectFull,
        finances: List<ProjectPartnerCoFinancing>,
        currencyResolver: (String) -> String?,
    ): PartnerReportIdentificationCreate {
        return partner.toReportIdentification(
                project = project,
                finances = finances,
                currencyResolver = currencyResolver,
            )
    }

    private fun validateMaxAmountOfReports(currentAmount: Int) {
        if (currentAmount >= MAX_REPORTS)
            throw MaxAmountOfReportsReachedException()
    }

    private fun validateProjectIsContracted(project: ProjectFull) {
        if (!project.projectStatus.status.isAlreadyContracted())
            throw ReportCanBeCreatedOnlyWhenContractedException()
    }

    private fun getCurrencyCodeForCountry(countryCode: String?, country: String?, currencyResolver: (String) -> String?): String? {
        var code = countryCode
        // prevent null when historic data did not map countryCode yet
        if (countryCode.isNullOrEmpty() && !country.isNullOrEmpty()) {
            code = getCountryCodeForCountry(country)
        }
        return if (code != null) {
            currencyResolver.invoke(code)
        } else {
            null
        }
    }

    private fun getCountryCodeForCountry(country: String) =
        Regex("\\(([A-Z]{2})\\)$").find(country)?.value?.substring(1, 3)

    private fun ProjectPartnerDetail.toReportIdentification(
        project: ProjectFull,
        finances: List<ProjectPartnerCoFinancing>,
        currencyResolver: (String) -> String?,
    ) = PartnerReportIdentificationCreate(
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
        countryCode = addresses.firstOrNull { it.type == ProjectPartnerAddressType.Organization }?.countryCode,
        coFinancing = finances,
    ).apply {
        currency = getCurrencyCodeForCountry(countryCode, country, currencyResolver)
    }

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

}
