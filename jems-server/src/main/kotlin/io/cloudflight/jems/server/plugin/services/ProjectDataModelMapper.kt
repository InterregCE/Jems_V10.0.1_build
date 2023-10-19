package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerContactDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.programme.lumpsum.ProgrammeLumpSumData
import io.cloudflight.jems.plugin.contract.models.programme.lumpsum.ProgrammeLumpSumPhaseData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammeObjectivePolicyData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammePriorityDataSimple
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammeSpecificObjectiveData
import io.cloudflight.jems.plugin.contract.models.programme.stateaid.ProgrammeStateAidData
import io.cloudflight.jems.plugin.contract.models.programme.stateaid.ProgrammeStateAidMeasureData
import io.cloudflight.jems.plugin.contract.models.programme.strategy.ProgrammeStrategyData
import io.cloudflight.jems.plugin.contract.models.programme.unitcost.BudgetCategoryData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.ProjectDataSectionA
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA4.ProjectResultIndicatorOverview
import io.cloudflight.jems.plugin.contract.models.project.sectionB.associatedOrganisation.ProjectAssociatedOrganizationAddressData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.associatedOrganisation.ProjectAssociatedOrganizationData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.NaceGroupLevelData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.PartnerSubTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectContactTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerAddressData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerAddressTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerContactData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerEssentialData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerMotivationData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerStateAidData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerVatRecoveryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetCostData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetCostsCalculationResultData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetGeneralCostEntryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetPeriodData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetStaffCostEntryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetTravelAndAccommodationCostEntryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetUnitCostEntryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.PartnerBudgetData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.plugin.contract.models.programme.unitcost.ProgrammeUnitCostListData
import io.cloudflight.jems.plugin.contract.models.project.ProjectIdentificationData
import io.cloudflight.jems.plugin.contract.models.project.contracting.ContractingDimensionCodeData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ApplicationStatusData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectStatusData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectAssessmentEligibilityResultData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectAssessmentEligibilityData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectAssessmentQualityResultData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectAssessmentQualityData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectDecisionData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectLifecycleData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA3.ProjectCoFinancingCategoryOverviewData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA3.ProjectCoFinancingOverviewData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA3.ProjectCoFinancingByFundOverviewData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA4.IndicatorOverviewLineWithCodes
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerBudgetOptionsData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingAndContributionData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingAndContributionSpfData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingFundTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerContributionData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerContributionSpfData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerContributionStatusData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerSummaryData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.ProjectDataSectionC
import io.cloudflight.jems.plugin.contract.models.project.sectionC.longTermPlans.ProjectLongTermPlansData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectCooperationCriteriaData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectHorizontalPrinciplesData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectHorizontalPrinciplesEffectData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectManagementData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.overallObjective.ProjectOverallObjectiveData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.partnership.ProjectPartnershipData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceBenefitData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceStrategyData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceSynergyData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectTargetGroupData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.results.ProjectResultData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.ProjectWorkPackageData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityDeliverableData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivitySummaryData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageInvestmentAddressData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageInvestmentData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageOutputData
import io.cloudflight.jems.plugin.contract.models.project.sectionD.BudgetCostsDetailData
import io.cloudflight.jems.plugin.contract.models.project.sectionD.ProjectBudgetOverviewPerPartnerPerPeriodData
import io.cloudflight.jems.plugin.contract.models.project.sectionD.ProjectPartnerBudgetPerFundData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.lumpsum.ProjectLumpSumData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.lumpsum.ProjectPartnerLumpSumData
import io.cloudflight.jems.plugin.contract.models.project.versions.ProjectVersionData
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingByFundOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingCategoryOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingOverview
import io.cloudflight.jems.server.project.service.contracting.model.ContractingDimensionCode
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectDescription
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.BudgetCostsDetail
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.model.ProjectContactType
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.model.ProjectRelevance
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceSynergy
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerContact
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.result.model.IndicatorOverviewLine
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivitySummary
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import java.time.LocalDate

fun ProjectFull.toDataModel(
    tableA3data: ProjectCoFinancingOverview,
    tableA4data: ProjectResultIndicatorOverview
) = pluginDataMapper.map(this, tableA3data, tableA4data)

fun ProjectFull.toIdentificationDataModel(
    projectStartDate: LocalDate?,
    projectEndDate: LocalDate?,
    programmeTitle: String,
    projectLifecycleData: ProjectLifecycleData
) = ProjectIdentificationData(
    id = id,
    customIdentifier = customIdentifier,
    acronym = acronym,
    status = projectStatus.status.toDataModel(),
    title = title.toDataModel(),
    intro = intro.toDataModel(),
    durationInMonths = duration,
    projectStartDate = projectStartDate,
    projectEndDate = projectEndDate,
    programmeTitle = programmeTitle,
    programmePriorityCode = programmePriority?.code,
    lifecycleData = projectLifecycleData
)

fun OutputProgrammePriorityPolicySimpleDTO.toDataModel() = pluginDataMapper.map(this)

fun OutputProgrammePrioritySimple.toDataModel() = pluginDataMapper.map(this)

fun ProjectDescription.toDataModel(workPackages: List<ProjectWorkPackageFull>, results: List<ProjectResult>) =
    pluginDataMapper.map(this, workPackages, results)

fun List<WorkPackageActivity>.toActivityDataModel() = map {
    WorkPackageActivityData(
        activityNumber = it.activityNumber,
        description = it.description.toDataModel(),
        title = it.title.toDataModel(),
        startPeriod = it.startPeriod,
        endPeriod = it.endPeriod,
        deliverables = it.deliverables.toDeliverableDataModel(),
        partnerIds = it.partnerIds,
        deactivated = it.deactivated
    )
}.toList()

fun List<WorkPackageActivityDeliverable>.toDeliverableDataModel() = map {
    WorkPackageActivityDeliverableData(
        deliverableNumber = it.deliverableNumber,
        description = it.description.toDataModel(),
        title = it.title.toDataModel(),
        period = it.period,
        deactivated = it.deactivated
    )
}.toList()

fun List<WorkPackageOutput>.toOutputDataModel() = map {
    WorkPackageOutputData(
        outputNumber = it.outputNumber,
        programmeOutputIndicatorId = it.programmeOutputIndicatorId,
        programmeOutputIndicatorIdentifier = it.programmeOutputIndicatorIdentifier,
        programmeOutputIndicatorName = it.programmeOutputIndicatorName.toDataModel(),
        programmeOutputIndicatorMeasurementUnit = it.programmeOutputIndicatorMeasurementUnit.toDataModel(),
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        periodStartMonth = it.periodStartMonth,
        periodEndMonth = it.periodEndMonth,
        description = it.description.toDataModel(),
        title = it.title.toDataModel(),
        deactivated = it.deactivated,
    )
}.toList()

fun List<IndicatorOverviewLine>.toIndicatorOverviewLines() = map {
    io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA4.IndicatorOverviewLine(
        outputIndicatorId = it.outputIndicator?.id,
        outputIndicatorIdentifier = it.outputIndicator?.identifier,
        outputIndicatorName = it.outputIndicator?.name.toDataModel(),
        outputIndicatorMeasurementUnit = it.outputIndicator?.measurementUnit.toDataModel(),
        outputIndicatorTargetValueSumUp = it.outputIndicator?.targetValueSumUp,

        projectOutputNumber = it.projectOutput?.projectOutputNumber,
        projectOutputTitle = it.projectOutput?.projectOutputTitle.toDataModel(),
        projectOutputTargetValue = it.projectOutput?.projectOutputTargetValue,

        resultIndicatorId = it.resultIndicator?.id,
        resultIndicatorIdentifier = it.resultIndicator?.identifier,
        resultIndicatorName = it.resultIndicator?.name.toDataModel(),
        resultIndicatorMeasurementUnit = it.resultIndicator?.measurementUnit.toDataModel(),
        resultIndicatorBaseline = it.resultIndicator?.baseline,
        resultIndicatorTargetValueSumUp = it.resultIndicator?.targetValueSumUp,

        onlyResultWithoutOutputs = it.onlyResultWithoutOutputs
    )
}

fun List<IndicatorOverviewLine>.toIndicatorOverviewLinesWithCodes() = map {
    IndicatorOverviewLineWithCodes(
        outputIndicatorId = it.outputIndicator?.id,
        outputIndicatorIdentifier = it.outputIndicator?.identifier,
        outputIndicatorName = it.outputIndicator?.name.toDataModel(),
        outputIndicatorMeasurementUnit = it.outputIndicator?.measurementUnit.toDataModel(),
        outputIndicatorTargetValueSumUp = it.outputIndicator?.targetValueSumUp,

        projectOutputNumber = it.projectOutput?.projectOutputNumber,
        projectOutputTitle = it.projectOutput?.projectOutputTitle.toDataModel(),
        projectOutputTargetValue = it.projectOutput?.projectOutputTargetValue,

        resultIndicatorId = it.resultIndicator?.id,
        resultIndicatorIdentifier = it.resultIndicator?.identifier,
        resultIndicatorName = it.resultIndicator?.name.toDataModel(),
        resultIndicatorMeasurementUnit = it.resultIndicator?.measurementUnit.toDataModel(),
        resultIndicatorBaseline = it.resultIndicator?.baseline,
        resultIndicatorTargetValueSumUp = it.resultIndicator?.targetValueSumUp,

        onlyResultWithoutOutputs = it.onlyResultWithoutOutputs,
        outputIndicatorCode = it.outputIndicator?.code,
        resultIndicatorCode = it.resultIndicator?.code
    )
}

fun ProjectPartnerBudgetOptions.toDataModel() = pluginDataMapper.map(this)

fun ProjectPartnerCoFinancingAndContribution.toDataModel() = pluginDataMapper.map(this)
fun ProjectPartnerCoFinancingAndContributionSpf.toDataModel() = pluginDataMapper.map(this)

fun BudgetCosts.toDataModel() = pluginDataMapper.map(this)
fun BudgetCostsCalculationResult.toDataModel() = pluginDataMapper.map(this)

fun ProjectPartnerDetail.toDataModel(stateAid: ProjectPartnerStateAid, budget: PartnerBudgetData, legalStatusDescription: Set<InputTranslation>) =
    pluginDataMapper.map(this, stateAid, budget, legalStatusDescription)

fun Iterable<OutputProjectAssociatedOrganizationDetail>.toDataModel() = map { pluginDataMapper.map(it) }.toSet()

fun List<ProjectLumpSum>.toDataModel(lumpSumsDetail: List<ProgrammeLumpSum>) =
    map { projectLumpSum -> pluginDataMapper.map(projectLumpSum, lumpSumsDetail.firstOrNull { it.id == projectLumpSum.programmeLumpSumId }) }

fun List<ProgrammeUnitCost>.toListDataModel() = map {
    ProgrammeUnitCostListData(
        id = it.id,
        name = it.name.mapTo(HashSet()) {
            InputTranslationData(it.language.toDataModel(), "E.2.1_${it.translation}")
        },
        type = it.type.toDataModel(),
        description = it.description.toDataModel(),
        costPerUnit = it.costPerUnit,
        categories = it.categories.mapTo(HashSet()) { BudgetCategoryData.valueOf(it.name) },
    )
}

fun List<ProjectVersion>.toDataModel() =
    map{pluginDataMapper.map(it)}

fun Set<InputTranslation>?.toDataModel() =
    this?.map { InputTranslationData(it.language.toDataModel(), it.translation) }?.toSet() ?: emptySet()

fun Iterable<ProjectPartnerDetail>.toProjectPartnerSummary() =
    map { pluginDataMapper.map(it) }

fun List<ProjectPartnerBudgetPerFund>.toProjectPartnerBudgetPerFundData() =
    this.map { pluginDataMapper.map(it) }

fun ProjectBudgetOverviewPerPartnerPerPeriod.toProjectBudgetOverviewPerPartnerPerPeriod() =
    pluginDataMapper.map(this)

fun SystemLanguage.toDataModel() =
    SystemLanguageData.valueOf(this.name)

fun ApplicationStatus.toDataModel() =
    ApplicationStatusData.valueOf(this.name)

fun ProjectAssessment.toDataModel() =
    pluginDataMapper.map(this)

fun ProgrammeFundType.toDataModel() =
    ProgrammeFundTypeData.valueOf(this.name)

fun List<ProjectCoFinancingByFundOverview>.projectCoFinancingByFundOverviewListToDataList() =
    map {
        ProjectCoFinancingByFundOverviewData(
            fundId= it.fundId,
            fundType= it.fundType?.toDataModel(),
            fundAbbreviation= it.fundAbbreviation.toDataModel(),
            fundingAmount= it.fundingAmount,
            coFinancingRate= it.coFinancingRate,
            autoPublicContribution= it.autoPublicContribution,
            otherPublicContribution= it.otherPublicContribution,
            totalPublicContribution= it.totalPublicContribution,
            privateContribution= it.privateContribution,
            totalContribution= it.totalContribution,
            totalFundAndContribution= it.totalFundAndContribution
        )
    }.toList()

fun List<ContractingDimensionCode>.toContractingDimensionCodeDataList() = map { pluginDataMapper.map(it) }


fun  ProjectPartnerDetail.toSummaryDataModel() = ProjectPartnerSummaryData(
    id = this.id,
    abbreviation = this.abbreviation,
    active = this.active,
    role = ProjectPartnerRoleData.valueOf(this.role.name),
    sortNumber = this.sortNumber,
    country = this.addresses.firstOrNull { it.type == ProjectPartnerAddressType.Organization }?.country,
    nutsRegion2 = this.addresses.firstOrNull { it.type == ProjectPartnerAddressType.Organization }?.nutsRegion2,
    vat = this.vat,
    vatRecovery = this.vatRecovery?.let { ProjectPartnerVatRecoveryData.valueOf(it.name) },
    otherIdentifierNumber = this.otherIdentifierNumber,
    otherIdentifierDescription = this.otherIdentifierDescription.toDataModel()
)

private val pluginDataMapper = Mappers.getMapper(PluginDataMapper::class.java)

@Mapper
abstract class PluginDataMapper {
    abstract fun map(partnerSubType: PartnerSubType): PartnerSubTypeData
    abstract fun map(naceGroupLevel: NaceGroupLevel): NaceGroupLevelData
    abstract fun map(applicationStatus: ApplicationStatus): ApplicationStatusData
    abstract fun map(projectAssessment: ProjectAssessment): ProjectDecisionData
    abstract fun map(projectAssessmentQuality: ProjectAssessmentQuality): ProjectAssessmentQualityData
    abstract fun map(projectAssessmentQualityResult: ProjectAssessmentQualityResult): ProjectAssessmentQualityResultData
    abstract fun map(projectAssessmentEligibility: ProjectAssessmentEligibility): ProjectAssessmentEligibilityData
    abstract fun map(projectAssessmentEligibilityResult: ProjectAssessmentEligibilityResult): ProjectAssessmentEligibilityResultData
    abstract fun map(projectStatus: ProjectStatus): ProjectStatusData
    abstract fun map(systemLanguage: SystemLanguage): SystemLanguageData
    abstract fun map(inputTranslation: InputTranslation): InputTranslationData
    abstract fun map(projectPartnerLumpSum: ProjectPartnerLumpSum): ProjectPartnerLumpSumData
    abstract fun map(budgetCategory: BudgetCategory): BudgetCategoryData
    abstract fun map(programmeLumpSumPhase: ProgrammeLumpSumPhase): ProgrammeLumpSumPhaseData
    abstract fun map(programmeLumpSum: ProgrammeLumpSum): ProgrammeLumpSumData
    abstract fun map(projectPartnerRoleDTO: ProjectPartnerRoleDTO): ProjectPartnerRoleData
    abstract fun map(projectPartnerSummaryDTO: ProjectPartnerSummaryDTO): ProjectPartnerEssentialData
    abstract fun map(outputProjectAssociatedOrganizationAddress: OutputProjectAssociatedOrganizationAddress): ProjectAssociatedOrganizationAddressData
    abstract fun map(outputProjectAssociatedOrganizationDetail: OutputProjectAssociatedOrganizationDetail): ProjectAssociatedOrganizationData
    abstract fun map(projectContactType: ProjectContactType): ProjectContactTypeData
    abstract fun map(projectPartnerContactDto: ProjectPartnerContactDTO): ProjectPartnerContactData
    abstract fun map(projectPartnerContact: ProjectPartnerContact): ProjectPartnerContactData
    abstract fun map(projectPartnerAddressType: ProjectPartnerAddressType): ProjectPartnerAddressTypeData
    abstract fun map(projectPartnerAddress: ProjectPartnerAddress): ProjectPartnerAddressData
    abstract fun map(projectPartnerRole: ProjectPartnerRole): ProjectPartnerRoleData
    abstract fun map(projectTargetGroup: ProjectTargetGroup): ProjectTargetGroupData
    abstract fun map(projectPartnerVatRecovery: ProjectPartnerVatRecovery): ProjectPartnerVatRecoveryData
    abstract fun map(budgetUnitCostEntry: BudgetUnitCostEntry): BudgetUnitCostEntryData
    abstract fun map(budgetGeneralCostEntry: BudgetGeneralCostEntry): BudgetGeneralCostEntryData
    abstract fun map(budgetTravelAndAccommodationCostEntry: BudgetTravelAndAccommodationCostEntry): BudgetTravelAndAccommodationCostEntryData
    abstract fun map(budgetPeriod: BudgetPeriod): BudgetPeriodData
    abstract fun map(budgetStaffCostEntry: BudgetStaffCostEntry): BudgetStaffCostEntryData
    abstract fun map(budgetCosts: BudgetCosts): BudgetCostData
    abstract fun map(projectPartnerContributionStatusDTO: ProjectPartnerContributionStatusDTO): ProjectPartnerContributionStatusData
    @Mappings(
        Mapping(target = "isPartner", source = "partner")
    )
    abstract fun map(projectPartnerContribution: ProjectPartnerContribution): ProjectPartnerContributionData
    abstract fun map(projectPartnerCoFinancingFundTypeDTO: ProjectPartnerCoFinancingFundTypeDTO): ProjectPartnerCoFinancingFundTypeData
    abstract fun map(projectPartnerCoFinancing: ProjectPartnerCoFinancing): ProjectPartnerCoFinancingData
    abstract fun map(projectPartnerCoFinancingAndContribution: ProjectPartnerCoFinancingAndContribution): ProjectPartnerCoFinancingAndContributionData
    abstract fun map(projectPartnerCoFinancingAndContributionSpf: ProjectPartnerCoFinancingAndContributionSpf): ProjectPartnerCoFinancingAndContributionSpfData
    abstract fun map(projectPartnerContributionSpf: ProjectPartnerContributionSpf) : ProjectPartnerContributionSpfData
    abstract fun map(projectPartnerBudgetOptions: ProjectPartnerBudgetOptions): ProjectPartnerBudgetOptionsData
    abstract fun map(address: Address): WorkPackageInvestmentAddressData
    abstract fun map(workPackageInvestment: WorkPackageInvestment): WorkPackageInvestmentData
    abstract fun map(workPackageInvestments: List<WorkPackageInvestment>): List<WorkPackageInvestmentData>
    abstract fun map(projectHorizontalPrinciplesEffect: ProjectHorizontalPrinciplesEffect): ProjectHorizontalPrinciplesEffectData
    abstract fun map(projectCooperationCriteria: ProjectCooperationCriteria): ProjectCooperationCriteriaData
    abstract fun map(projectManagement: ProjectManagement): ProjectManagementData
    abstract fun map(projectRelevanceStrategy: ProjectRelevanceStrategy): ProjectRelevanceStrategyData
    abstract fun map(projectRelevanceBenefit: ProjectRelevanceBenefit): ProjectRelevanceBenefitData
    abstract fun map(projectRelevance: ProjectRelevance): ProjectRelevanceData
    abstract fun map(outputProgrammePrioritySimple: OutputProgrammePrioritySimple): ProgrammePriorityDataSimple
    abstract fun map(programmeObjectivePolicy: ProgrammeObjectivePolicy): ProgrammeObjectivePolicyData
    abstract fun map(outputProgrammePriorityPolicySimpleDTO: OutputProgrammePriorityPolicySimpleDTO): ProgrammeSpecificObjectiveData
    abstract fun map(coFinancingOverview: ProjectCoFinancingOverview): ProjectCoFinancingOverviewData
    @Mappings(
        Mapping(target = "coFinancingOverview", source = "tableA3data"),
        Mapping(target = "resultIndicatorOverview", source = "tableA4data")
    )
    abstract fun map(projectFull: ProjectFull,
                     tableA3data: ProjectCoFinancingOverview,
                     tableA4data: ProjectResultIndicatorOverview): ProjectDataSectionA
    abstract fun map(projectHorizontalPrinciples: ProjectHorizontalPrinciples): ProjectHorizontalPrinciplesData
    abstract fun map(programmeStateAidMeasure: ProgrammeStateAidMeasure): ProgrammeStateAidMeasureData
    abstract fun map(workPackageActivitySummary: WorkPackageActivitySummary): WorkPackageActivitySummaryData
    abstract fun map(programmeStateAid: ProgrammeStateAid): ProgrammeStateAidData
    abstract fun map(projectPartnerStateAid: ProjectPartnerStateAid): ProjectPartnerStateAidData
    abstract fun map(projectResult: ProjectResult): ProjectResultData
    abstract fun map(budgetCostsCalculationResult: BudgetCostsCalculationResult): BudgetCostsCalculationResultData
    abstract fun map(projectVersion: ProjectVersion): ProjectVersionData
    abstract fun map(projectPartnerBudgetPerFund: ProjectPartnerBudgetPerFund): ProjectPartnerBudgetPerFundData
    abstract fun map(budgetCostsDetail: BudgetCostsDetail): BudgetCostsDetailData
    abstract fun map(projectBudgetOverviewPerPartnerPerPeriod: ProjectBudgetOverviewPerPartnerPerPeriod): ProjectBudgetOverviewPerPartnerPerPeriodData
    abstract fun map(contractingDimensionCodes: ContractingDimensionCode): ContractingDimensionCodeData

    @Mappings(
        Mapping(target = "programmeLumpSum", source = "lumpSumsDetail")
    )
    abstract fun map(projectLumpSum: ProjectLumpSum, lumpSumsDetail: ProgrammeLumpSum?): ProjectLumpSumData

    @Mappings(
        Mapping(target = "projectWorkPackages", source = "workPackages"),
        Mapping(target = "projectResults", source = "results"),
    )
    abstract fun map(
        description: ProjectDescription, workPackages: List<ProjectWorkPackageFull>, results: List<ProjectResult>
    ): ProjectDataSectionC

    @Mappings(
        Mapping(target = "stateAid", source = "stateAid"),
        Mapping(target = "budget", source = "budget"),
        Mapping(target = "legalStatusDescription", source = "legalStatusDescription"),
    )
    abstract fun map(
        projectPartnerDetail: ProjectPartnerDetail, stateAid: ProjectPartnerStateAid, budget: PartnerBudgetData, legalStatusDescription: Set<InputTranslation>
    ): ProjectPartnerData

    @Mappings(
        Mapping(target = "nutsRegion2", source = "region"),
    )
    abstract fun map(projectPartnerSummary: ProjectPartnerSummary): ProjectPartnerSummaryData

    fun mapToProjectWorkPackageData(projectWorkPackageFull: List<ProjectWorkPackageFull>) =
        projectWorkPackageFull.map {
            ProjectWorkPackageData(
                id = it.id,
                workPackageNumber = it.workPackageNumber,
                name = it.name.toDataModel(),
                specificObjective = it.specificObjective.toDataModel(),
                objectiveAndAudience = it.objectiveAndAudience.toDataModel(),
                activities = it.activities.toActivityDataModel(),
                outputs = it.outputs.toOutputDataModel(),
                investments = pluginDataMapper.map(it.investments),
                deactivated = it.deactivated,
            )
        }.toList()

    // custom mapping to avoid mapping exception when all kotlin data class's properties are optional and there is at least one immutable property

    fun map(projectPartnerMotivation: ProjectPartnerMotivation?): ProjectPartnerMotivationData =
        ProjectPartnerMotivationData(
            organizationRelevance = projectPartnerMotivation?.organizationRelevance.toDataModel(),
            organizationRole = projectPartnerMotivation?.organizationRole.toDataModel(),
            organizationExperience = projectPartnerMotivation?.organizationExperience.toDataModel()
        )

    fun map(projectLongTermPlans: ProjectLongTermPlans?): ProjectLongTermPlansData =
        ProjectLongTermPlansData(
            projectOwnership = projectLongTermPlans?.projectOwnership.toDataModel(),
            projectDurability = projectLongTermPlans?.projectDurability.toDataModel(),
            projectTransferability = projectLongTermPlans?.projectTransferability.toDataModel(),
        )

    fun map(projectPartnership: ProjectPartnership?): ProjectPartnershipData =
        ProjectPartnershipData(projectPartnership?.partnership.toDataModel())

    fun map(projectRelevanceSynergy: ProjectRelevanceSynergy?): ProjectRelevanceSynergyData =
        ProjectRelevanceSynergyData(
            synergy = projectRelevanceSynergy?.synergy.toDataModel(),
            specification = projectRelevanceSynergy?.specification.toDataModel()
        )

    fun map(projectOverallObjective: ProjectOverallObjective?): ProjectOverallObjectiveData =
        ProjectOverallObjectiveData(projectOverallObjective?.overallObjective.toDataModel())

    fun map(projectPartnerDetail: ProjectPartnerDetail): ProjectPartnerSummary =
        ProjectPartnerSummary(
            id = projectPartnerDetail.id,
            abbreviation = projectPartnerDetail.abbreviation,
            active = projectPartnerDetail.active,
            role = ProjectPartnerRole.valueOf(projectPartnerDetail.role.name),
            sortNumber = projectPartnerDetail.sortNumber,
            country = projectPartnerDetail.addresses.firstOrNull { it.type == ProjectPartnerAddressType.Organization }?.country,
            region = projectPartnerDetail.addresses.firstOrNull { it.type == ProjectPartnerAddressType.Organization }?.nutsRegion2
        )

    fun map(programmeStrategy: ProgrammeStrategy): ProgrammeStrategyData {
        val programmeStrategyData: ProgrammeStrategyData = when (programmeStrategy) {
            ProgrammeStrategy.EUStrategyAdriaticIonianRegion -> ProgrammeStrategyData.EUStrategyAdriaticIonianRegion
            ProgrammeStrategy.EUStrategyAlpineRegion -> ProgrammeStrategyData.EUStrategyAlpineRegion
            ProgrammeStrategy.EUStrategyBalticSeaRegion -> ProgrammeStrategyData.EUStrategyBalticSeaRegion
            ProgrammeStrategy.EUStrategyDanubeRegion -> ProgrammeStrategyData.EUStrategyDanubeRegion
            ProgrammeStrategy.SeaBasinStrategyNorthSea -> ProgrammeStrategyData.SeaBasinStrategyNorthSea
            ProgrammeStrategy.SeaBasinStrategyBlackSea -> ProgrammeStrategyData.SeaBasinStrategyBlackSea
            ProgrammeStrategy.SeaBasinStrategyBalticSea -> ProgrammeStrategyData.EUStrategyBalticSeaRegion
            ProgrammeStrategy.SeaBasinStrategyArcticOcean -> ProgrammeStrategyData.SeaBasinStrategyArcticOcean
            ProgrammeStrategy.SeaBasinStrategyOutermostRegions -> ProgrammeStrategyData.SeaBasinStrategyOutermostRegions
            ProgrammeStrategy.SeaBasinStrategyAdriaticIonianSea -> ProgrammeStrategyData.EUStrategyAdriaticIonianRegion
            ProgrammeStrategy.MediterraneanSeaBasin -> ProgrammeStrategyData.MediterraneanSeaBasin
            ProgrammeStrategy.AtlanticStrategy -> ProgrammeStrategyData.AtlanticStrategy
            ProgrammeStrategy.EuropeanGreenDeal -> ProgrammeStrategyData.EuropeanGreenDeal
            ProgrammeStrategy.TerritorialAgenda2030 -> ProgrammeStrategyData.TerritorialAgenda2030
            ProgrammeStrategy.Other -> ProgrammeStrategyData.Other
            else -> throw IllegalArgumentException("Unexpected enum constant: $programmeStrategy")
        }
        return programmeStrategyData
    }


    fun map(coFinancingCategoryOverview: ProjectCoFinancingCategoryOverview): ProjectCoFinancingCategoryOverviewData =
        ProjectCoFinancingCategoryOverviewData(
            fundOverviews = coFinancingCategoryOverview.fundOverviews.projectCoFinancingByFundOverviewListToDataList(),
            totalFundingAmount = coFinancingCategoryOverview.totalFundingAmount,
            totalEuFundingAmount = coFinancingCategoryOverview.totalEuFundingAmount,
            averageCoFinancingRate = coFinancingCategoryOverview.averageCoFinancingRate,
            averageEuFinancingRate = coFinancingCategoryOverview.averageEuFinancingRate,
            totalAutoPublicContribution = coFinancingCategoryOverview.totalAutoPublicContribution,
            totalEuAutoPublicContribution = coFinancingCategoryOverview.totalEuAutoPublicContribution,
            totalOtherPublicContribution = coFinancingCategoryOverview.totalOtherPublicContribution,
            totalEuOtherPublicContribution = coFinancingCategoryOverview.totalEuOtherPublicContribution,
            totalPublicContribution = coFinancingCategoryOverview.totalPublicContribution,
            totalEuPublicContribution = coFinancingCategoryOverview.totalEuPublicContribution,
            totalPrivateContribution = coFinancingCategoryOverview.totalPrivateContribution,
            totalEuPrivateContribution = coFinancingCategoryOverview.totalEuPrivateContribution,
            totalContribution = coFinancingCategoryOverview.totalContribution,
            totalEuContribution = coFinancingCategoryOverview.totalEuContribution,
            totalFundAndContribution = coFinancingCategoryOverview.totalFundAndContribution,
            totalEuFundAndContribution = coFinancingCategoryOverview.totalEuFundAndContribution
        )


}
