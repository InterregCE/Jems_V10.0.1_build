package io.cloudflight.jems.server.project.service.report.partner.base.createProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.getCountryCodeForCountry
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReportNotSpecific
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportBaseData
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportIdentificationCreate
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.PartnerReportInvestmentSummary
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportCreatePersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.partnerReportCreated
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
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
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportCreatePersistence: ProjectPartnerReportCreatePersistence,
    private val currencyPersistence: CurrencyPersistence,
    private val createProjectPartnerReportBudget: CreateProjectPartnerReportBudget,
    private val auditPublisher: ApplicationEventPublisher,
) : CreateProjectPartnerReportInteractor {

    companion object {
        private const val MAX_REPORTS = 100 // Previous value = 25
    }

    @CanEditPartnerReportNotSpecific
    @Transactional
    @ExceptionWrapper(CreateProjectPartnerReportException::class)
    override fun createReportFor(partnerId: Long): ProjectPartnerReportSummary {
        val projectId = projectPartnerPersistence.getProjectIdForPartnerId(partnerId)
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)

        val project = projectPersistence.getProject(projectId = projectId, version = version)

        validateMaxAmountOfReports(currentAmount = reportPersistence.countForPartner(partnerId = partnerId))
        validateNoReOpenedReports(reportPersistence.existsByStatusIn(partnerId, ReportStatus.ARE_LAST_OPEN_STATUSES))
        validateProjectIsContracted(project)

        val baseData = generateReportBaseData(
            partnerId = partnerId,
            latestReportNumber = reportPersistence.getCurrentLatestReportForPartner(partnerId)?.reportNumber ?: 0,
            version = version,
        )

        val partner = projectPartnerPersistence.getById(partnerId, version)
        val identification = generateReportIdentification(
            partner = partner,
            project = project,
            currencyResolver = { currencyPersistence.getCurrencyForCountry(it) },
        )

        val workPackages = projectWorkPackagePersistence
            .getWorkPackagesWithAllDataByProjectId(projectId = project.id!!, version = version)

        val budget = createProjectPartnerReportBudget.retrieveBudgetDataFor(
            projectId = projectId,
            partner = partner.toSummary(),
            version = version,
            coFinancing = PartnerReportCoFinancing(
                coFinancing = partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, version),
                coFinancingSpf = partnerCoFinancingPersistence.getSpfCoFinancingAndContributions(partnerId, version),
            ),
            investments = workPackages.extractInvestments(),
        )

        val report = ProjectPartnerReportCreate(
            baseData = baseData,
            identification = identification,
            workPackages = workPackages.toCreateEntity(),
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
        currencyResolver: (String) -> String?,
    ) = partner.toReportIdentification(project = project, currencyResolver = currencyResolver)

    private fun validateMaxAmountOfReports(currentAmount: Int) {
        if (currentAmount >= MAX_REPORTS)
            throw MaxAmountOfReportsReachedException()
    }

    private fun validateNoReOpenedReports(areThereLastReOpenedReports: Boolean) {
        if (areThereLastReOpenedReports)
            throw LastReOpenedReportException()
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

    private fun ProjectPartnerDetail.toReportIdentification(
        project: ProjectFull,
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
    ).apply {
        currency = getCurrencyCodeForCountry(countryCode, country, currencyResolver)
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
