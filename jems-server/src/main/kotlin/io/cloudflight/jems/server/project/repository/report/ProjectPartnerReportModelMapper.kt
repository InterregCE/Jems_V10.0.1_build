package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.repository.legalstatus.toEntity
import io.cloudflight.jems.server.programme.repository.legalstatus.toStatusModel
import io.cloudflight.jems.server.project.entity.report.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.repositoryModel.ReportSummary
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.create.PreviouslyReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.create.PreviouslyReportedFund
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageOutput
import java.math.BigDecimal.ZERO

fun ReportSummary.toModelSummary() = ProjectPartnerReportSummary(
    id = id,
    reportNumber = number,
    status = status,
    version = version,
    firstSubmission = firstSubmission,
    createdAt = createdAt,
    startDate = startDate,
    endDate = endDate,
    periodDetail = getPeriodData(),
)

fun ProjectPartnerReportEntity.toCreateModelSummary() = ProjectPartnerReportSummary(
    id = id,
    reportNumber = number,
    status = status,
    version = applicationFormVersion,
    firstSubmission = firstSubmission,
    createdAt = createdAt,
    startDate = null,
    endDate = null,
    periodDetail = null,
)

fun ProjectPartnerReportEntity.toSubmissionSummary() = ProjectPartnerReportSubmissionSummary(
    id = id,
    reportNumber = number,
    status = status,
    version = applicationFormVersion,
    firstSubmission = firstSubmission,
    createdAt = createdAt,
    projectIdentifier = identification.projectIdentifier,
    projectAcronym = identification.projectAcronym,
    partnerNumber = identification.partnerNumber,
    partnerRole = identification.partnerRole,
)

fun ProjectPartnerReportEntity.toModel(coFinancing: List<ProjectPartnerReportCoFinancingEntity>) = ProjectPartnerReport(
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
            previouslyReported = fund.previouslyReported,
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
    )
}

fun ReportProjectFileEntity.toModel() = ProjectReportFileMetadata(
    id = id,
    name = name,
    uploaded = uploaded,
)

fun ReportExpenditureCostCategory.toCreateEntity(report: ProjectPartnerReportEntity) = ReportProjectPartnerExpenditureCostCategoryEntity(
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
)
