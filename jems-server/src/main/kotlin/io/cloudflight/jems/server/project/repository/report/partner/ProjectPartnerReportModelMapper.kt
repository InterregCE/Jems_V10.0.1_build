package io.cloudflight.jems.server.project.repository.report.partner

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.repository.legalstatus.toEntity
import io.cloudflight.jems.server.programme.repository.legalstatus.toStatusModel
import io.cloudflight.jems.server.project.entity.report.partner.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.partner.model.CertificateSummary
import io.cloudflight.jems.server.project.repository.report.partner.model.ReportSummary
import io.cloudflight.jems.server.project.repository.report.partner.model.ReportIdentificationSummary
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedFund
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectPartnerReportIdentificationSummary
import java.math.BigDecimal.ZERO
import java.time.ZoneId

fun ReportSummary.toModelSummary() =
    ProjectPartnerReportSummary(
        id = id,
        reportNumber = number,
        status = status,
        version = version,
        firstSubmission = firstSubmission,
        controlEnd = controlEnd,
        createdAt = createdAt,
        startDate = startDate,
        endDate = endDate,
        periodDetail = getPeriodData(),
        projectReportId = projectReportId,
        projectReportNumber = projectReportNumber,
        totalEligibleAfterControl = totalEligibleAfterControl,
        deletable = false,
    )

fun CertificateSummary.toModel() =
    PartnerReportCertificate(
        partnerReportId = partnerReportId,
        partnerReportNumber = partnerReportNumber,

        partnerId = partnerId,
        partnerRole = partnerRole,
        partnerNumber = partnerNumber,

        controlEnd = controlEnd.atZone(ZoneId.systemDefault()),
        totalEligibleAfterControl = totalEligibleAfterControl,
        projectReportId = projectReportId,
        projectReportNumber = projectReportNumber,
    )

fun ProjectPartnerReportEntity.toModelSummaryAfterCreate() =
    ProjectPartnerReportSummary(
        id = id,
        reportNumber = number,
        status = status,
        version = applicationFormVersion,
        firstSubmission = firstSubmission,
        controlEnd = controlEnd,
        createdAt = createdAt,
        startDate = null,
        endDate = null,
        periodDetail = null,
        projectReportId = projectReport?.id,
        projectReportNumber = projectReport?.number,
        totalEligibleAfterControl = null,
        deletable = false,
    )

fun ProjectPartnerReportEntity.toSubmissionSummary() =
    ProjectPartnerReportSubmissionSummary(
        id = id,
        reportNumber = number,
        status = status,
        version = applicationFormVersion,
        firstSubmission = firstSubmission,
        controlEnd = controlEnd,
        createdAt = createdAt,
        projectIdentifier = identification.projectIdentifier,
        projectAcronym = identification.projectAcronym,
        partnerNumber = identification.partnerNumber,
        partnerRole = identification.partnerRole,
    )

fun List<ReportIdentificationSummary>.toIdentificationSummaries():List<ProjectPartnerReportIdentificationSummary> = map {
    ProjectPartnerReportIdentificationSummary(
        id = it.partnerReportId,
        reportNumber = it.partnerReportNumber,
        partnerNumber = it.partnerNumber,
        partnerRole = it.partnerRole,
        partnerId = it.partnerId,
        nextReportForecast = it.nextReportForecast ?: ZERO,
        periodDetail = it.getPeriodData(),
        sumTotalEligibleAfterControl = it.totalEligibleAfterControl ?: ZERO,
    )
}

fun ProjectPartnerReportEntity.toModel(coFinancing: List<ProjectPartnerReportCoFinancingEntity>) =
    ProjectPartnerReport(
        id = id,
        reportNumber = number,
        status = status,
        version = applicationFormVersion,
        firstSubmission = firstSubmission,

        identification = PartnerReportIdentification(
            projectIdentifier = identification.projectIdentifier,
            projectAcronym = identification.projectAcronym,
            partnerNumber = identification.partnerNumber,
            partnerAbbreviation = identification.partnerAbbreviation,
            partnerRole = identification.partnerRole,
            nameInOriginalLanguage = identification.nameInOriginalLanguage,
            nameInEnglish = identification.nameInEnglish,
            legalStatus = identification.legalStatus?.toStatusModel(),
            partnerType = identification.partnerType,
            vatRecovery = identification.vatRecovery,
            coFinancing = coFinancing.toModel(),
            country = identification.country,
            currency = identification.currency
        )
    )


fun PartnerReportIdentification.toEntity() = PartnerReportIdentificationEntity(
    projectIdentifier = projectIdentifier,
    projectAcronym = projectAcronym,
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    partnerRole = partnerRole,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    legalStatus = legalStatus?.toEntity(),
    partnerType = partnerType,
    vatRecovery = vatRecovery,
    country = country,
    currency = currency
)

fun List<ProjectPartnerReportCoFinancingEntity>.toModel() = map {
    ProjectPartnerCoFinancing(
        fundType = it.programmeFund?.let { MainFund } ?: PartnerContribution,
        fund = it.programmeFund?.toModel(),
        percentage = it.percentage,
    )
}

fun ProjectPartnerReportCreate.toEntity(
    legalStatus: ProgrammeLegalStatusEntity?
) = ProjectPartnerReportEntity(
    partnerId = baseData.partnerId,
    number = baseData.reportNumber,
    status = baseData.status,
    applicationFormVersion = baseData.version,
    firstSubmission = null,
    controlEnd = null,

    identification = PartnerReportIdentificationEntity(
        projectIdentifier = identification.projectIdentifier,
        projectAcronym = identification.projectAcronym,
        partnerNumber = identification.partnerNumber,
        partnerAbbreviation = identification.partnerAbbreviation,
        partnerRole = identification.partnerRole,
        nameInOriginalLanguage = identification.nameInOriginalLanguage,
        nameInEnglish = identification.nameInEnglish,
        legalStatus = legalStatus,
        partnerType = identification.partnerType,
        vatRecovery = identification.vatRecovery,
        country = identification.country,
        currency = identification.currency
    ),
    projectReport = null,
)

fun List<PreviouslyReportedFund>.toEntity(
    reportEntity: ProjectPartnerReportEntity,
    programmeFundResolver: (Long) -> ProgrammeFundEntity,
): List<ProjectPartnerReportCoFinancingEntity> {
    return mapIndexed { index, fund ->
        ProjectPartnerReportCoFinancingEntity(
            id = ProjectPartnerReportCoFinancingIdEntity(reportEntity, index.plus(1)),
            programmeFund = fund.fundId?.let { programmeFundResolver.invoke(it) },
            percentage = fund.percentage,
            total = fund.total,
            current = ZERO,
            totalEligibleAfterControl = ZERO,
            previouslyReported = fund.previouslyReported,
            previouslyPaid = fund.previouslyPaid,
        )
    }
}

fun PreviouslyReportedCoFinancing.toEntity(
    reportEntity: ProjectPartnerReportEntity,
): ReportProjectPartnerExpenditureCoFinancingEntity {
    return ReportProjectPartnerExpenditureCoFinancingEntity(
        reportEntity = reportEntity,

        partnerContributionTotal = totalPartner,
        publicContributionTotal = totalPublic,
        automaticPublicContributionTotal = totalAutoPublic,
        privateContributionTotal = totalPrivate,
        sumTotal = totalSum,

        partnerContributionCurrent = ZERO,
        publicContributionCurrent = ZERO,
        automaticPublicContributionCurrent = ZERO,
        privateContributionCurrent = ZERO,
        sumCurrent = ZERO,

        partnerContributionTotalEligibleAfterControl = ZERO,
        publicContributionTotalEligibleAfterControl = ZERO,
        automaticPublicContributionTotalEligibleAfterControl = ZERO,
        privateContributionTotalEligibleAfterControl = ZERO,
        sumTotalEligibleAfterControl = ZERO,

        partnerContributionPreviouslyReported = previouslyReportedPartner,
        publicContributionPreviouslyReported = previouslyReportedPublic,
        automaticPublicContributionPreviouslyReported = previouslyReportedAutoPublic,
        privateContributionPreviouslyReported = previouslyReportedPrivate,
        sumPreviouslyReported = previouslyReportedSum,
    )
}

fun List<ProjectPartnerReportWorkPackageEntity>.toModel(
    retrieveActivities: (ProjectPartnerReportWorkPackageEntity) -> List<ProjectPartnerReportWorkPackageActivityEntity>,
    retrieveDeliverables: (ProjectPartnerReportWorkPackageActivityEntity) -> List<ProjectPartnerReportWorkPackageActivityDeliverableEntity>,
    retrieveOutputs: (ProjectPartnerReportWorkPackageEntity) -> List<ProjectPartnerReportWorkPackageOutputEntity>,
) = map {
    ProjectPartnerReportWorkPackage(
        id = it.id,
        number = it.number,
        description = it.translatedValues.extractField { it.description },
        activities = retrieveActivities.invoke(it).toActivitiesModel(retrieveDeliverables),
        outputs = retrieveOutputs.invoke(it).toOutputsModel(),
    )
}

fun List<ProjectPartnerReportWorkPackageActivityEntity>.toActivitiesModel(
    retrieveDeliverables: (ProjectPartnerReportWorkPackageActivityEntity) -> List<ProjectPartnerReportWorkPackageActivityDeliverableEntity>,
) = map {
    ProjectPartnerReportWorkPackageActivity(
        id = it.id,
        number = it.number,
        title = it.translatedValues.extractField { it.title },
        progress = it.translatedValues.extractField { it.description },
        attachment = it.attachment?.toModel(),
        deliverables = retrieveDeliverables.invoke(it).toDeliverablesModel(),
        deactivated = it.deactivated
    )
}

fun List<ProjectPartnerReportWorkPackageActivityDeliverableEntity>.toDeliverablesModel() = map {
    ProjectPartnerReportWorkPackageActivityDeliverable(
        id = it.id,
        number = it.number,
        title = it.translatedValues.extractField { it.title },
        contribution = it.contribution,
        evidence = it.evidence,
        attachment = it.attachment?.toModel(),
        deactivated = it.deactivated
    )
}

fun List<ProjectPartnerReportWorkPackageOutputEntity>.toOutputsModel() = map {
    ProjectPartnerReportWorkPackageOutput(
        id = it.id,
        number = it.number,
        title = it.translatedValues.extractField { it.title },
        contribution = it.contribution,
        evidence = it.evidence,
        attachment = it.attachment?.toModel(),
        deactivated = it.deactivated,
    )
}

fun JemsFileMetadataEntity.toModel() = JemsFileMetadata(
    id = id,
    name = name,
    uploaded = uploaded,
)

fun ReportExpenditureCostCategory.toCreateEntity(report: ProjectPartnerReportEntity) =
    ReportProjectPartnerExpenditureCostCategoryEntity(
        reportEntity = report,
        officeAndAdministrationOnStaffCostsFlatRate = options.officeAndAdministrationOnStaffCostsFlatRate,
        officeAndAdministrationOnDirectCostsFlatRate = options.officeAndAdministrationOnDirectCostsFlatRate,
        travelAndAccommodationOnStaffCostsFlatRate = options.travelAndAccommodationOnStaffCostsFlatRate,
        staffCostsFlatRate = options.staffCostsFlatRate,
        otherCostsOnStaffCostsFlatRate = options.otherCostsOnStaffCostsFlatRate,

        staffTotal = totalsFromAF.staff,
        officeTotal = totalsFromAF.office,
        travelTotal = totalsFromAF.travel,
        externalTotal = totalsFromAF.external,
        equipmentTotal = totalsFromAF.equipment,
        infrastructureTotal = totalsFromAF.infrastructure,
        otherTotal = totalsFromAF.other,
        lumpSumTotal = totalsFromAF.lumpSum,
        unitCostTotal = totalsFromAF.unitCost,
        sumTotal = totalsFromAF.sum,

        staffCurrent = ZERO,
        officeCurrent = ZERO,
        travelCurrent = ZERO,
        externalCurrent = ZERO,
        equipmentCurrent = ZERO,
        infrastructureCurrent = ZERO,
        otherCurrent = ZERO,
        lumpSumCurrent = ZERO,
        unitCostCurrent = ZERO,
        sumCurrent = ZERO,

        staffTotalEligibleAfterControl = ZERO,
        officeTotalEligibleAfterControl = ZERO,
        travelTotalEligibleAfterControl = ZERO,
        externalTotalEligibleAfterControl = ZERO,
        equipmentTotalEligibleAfterControl = ZERO,
        infrastructureTotalEligibleAfterControl = ZERO,
        otherTotalEligibleAfterControl = ZERO,
        lumpSumTotalEligibleAfterControl = ZERO,
        unitCostTotalEligibleAfterControl = ZERO,
        sumTotalEligibleAfterControl = ZERO,

        staffPreviouslyReported = previouslyReported.staff,
        officePreviouslyReported = previouslyReported.office,
        travelPreviouslyReported = previouslyReported.travel,
        externalPreviouslyReported = previouslyReported.external,
        equipmentPreviouslyReported = previouslyReported.equipment,
        infrastructurePreviouslyReported = previouslyReported.infrastructure,
        otherPreviouslyReported = previouslyReported.other,
        lumpSumPreviouslyReported = previouslyReported.lumpSum,
        unitCostPreviouslyReported = previouslyReported.unitCost,
        sumPreviouslyReported = previouslyReported.sum,

        // Parking

        staffCurrentParked = ZERO,
        officeCurrentParked = ZERO,
        travelCurrentParked = ZERO,
        externalCurrentParked = ZERO,
        equipmentCurrentParked = ZERO,
        infrastructureCurrentParked = ZERO,
        otherCurrentParked = ZERO,
        lumpSumCurrentParked = ZERO,
        unitCostCurrentParked = ZERO,
        sumCurrentParked = ZERO,

        staffCurrentReIncluded = ZERO,
        officeCurrentReIncluded = ZERO,
        travelCurrentReIncluded = ZERO,
        externalCurrentReIncluded = ZERO,
        equipmentCurrentReIncluded = ZERO,
        infrastructureCurrentReIncluded = ZERO,
        otherCurrentReIncluded = ZERO,
        lumpSumCurrentReIncluded = ZERO,
        unitCostCurrentReIncluded = ZERO,
        sumCurrentReIncluded = ZERO,

        staffPreviouslyReportedParked = previouslyReportedParked.staff,
        officePreviouslyReportedParked = previouslyReportedParked.office,
        travelPreviouslyReportedParked = previouslyReportedParked.travel,
        externalPreviouslyReportedParked = previouslyReportedParked.external,
        equipmentPreviouslyReportedParked = previouslyReportedParked.equipment,
        infrastructurePreviouslyReportedParked = previouslyReportedParked.infrastructure,
        otherPreviouslyReportedParked = previouslyReportedParked.other,
        lumpSumPreviouslyReportedParked = previouslyReportedParked.lumpSum,
        unitCostPreviouslyReportedParked = previouslyReportedParked.unitCost,
        sumPreviouslyReportedParked = previouslyReportedParked.sum,

    )
