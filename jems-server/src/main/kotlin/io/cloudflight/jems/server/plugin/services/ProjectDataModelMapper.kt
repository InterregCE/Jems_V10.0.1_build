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
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ApplicationStatusData
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
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerBudgetOptionsData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingAndContributionData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingFundTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerContributionData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerContributionStatusData
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
import io.cloudflight.jems.plugin.contract.models.project.sectionD.ProjectPartnerBudgetPerFundData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.ProjectDataSectionE
import io.cloudflight.jems.plugin.contract.models.project.sectionE.lumpsum.ProjectLumpSumData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.lumpsum.ProjectPartnerLumpSumData
import io.cloudflight.jems.plugin.contract.models.project.versions.ProjectVersionData
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingOverview
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.model.ProjectContactType
import io.cloudflight.jems.server.project.service.model.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.service.model.ProjectDescription
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectRelevance
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceSynergy
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
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

fun ProjectFull.toDataModel(
    tableA3data: ProjectCoFinancingOverview,
    tableA4data: ProjectResultIndicatorOverview
) = pluginDataMapper.map(this, tableA3data, tableA4data)

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
        partnerIds = it.partnerIds
    )
}.toList()

fun List<WorkPackageActivityDeliverable>.toDeliverableDataModel() = map {
    WorkPackageActivityDeliverableData(
        deliverableNumber = it.deliverableNumber,
        description = it.description.toDataModel(),
        title = it.title.toDataModel(),
        period = it.period
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
        title = it.title.toDataModel()
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

        onlyResultWithoutOutputs = it.onlyResultWithoutOutputs,
    )
}

fun ProjectPartnerBudgetOptions.toDataModel() = pluginDataMapper.map(this)

fun ProjectPartnerCoFinancingAndContribution.toDataModel() = pluginDataMapper.map(this)

fun BudgetCosts.toDataModel() = pluginDataMapper.map(this)
fun BudgetCostsCalculationResult.toDataModel() = pluginDataMapper.map(this)

fun ProjectPartnerDetail.toDataModel(stateAid: ProjectPartnerStateAid, budget: PartnerBudgetData, legalStatusDescription: Set<InputTranslation>) =
    pluginDataMapper.map(this, stateAid, budget, legalStatusDescription)

fun Iterable<OutputProjectAssociatedOrganizationDetail>.toDataModel() = map { pluginDataMapper.map(it) }.toSet()

fun List<ProjectLumpSum>.toDataModel(lumpSumsDetail: List<ProgrammeLumpSum>) =
    ProjectDataSectionE(
        map { projectLumpSum -> pluginDataMapper.map(projectLumpSum, lumpSumsDetail.firstOrNull { it.id == projectLumpSum.programmeLumpSumId }) }
    )

fun List<ProjectVersion>.toDataModel() =
    map{pluginDataMapper.map(it)}

fun Set<InputTranslation>?.toDataModel() =
    this?.map { InputTranslationData(it.language.toDataModel(), it.translation) }?.toSet() ?: emptySet()

fun Set<ProjectPartnerData>.toProjectPartnerSummary() =
    map { pluginDataMapper.map(it) }

fun List<ProjectPartnerBudgetPerFund>.toProjectPartnerBudgetPerFundData() =
    this.map { pluginDataMapper.map(it) }

fun SystemLanguage.toDataModel() =
    SystemLanguageData.valueOf(this.name)

fun ApplicationStatus.toDataModel() =
    ApplicationStatusData.valueOf(this.name)

private val pluginDataMapper = Mappers.getMapper(PluginDataMapper::class.java)

@Mapper
abstract class PluginDataMapper {
    abstract fun map(partnerSubType: PartnerSubType): PartnerSubTypeData
    abstract fun map(naceGroupLevel: NaceGroupLevel): NaceGroupLevelData
    abstract fun map(applicationStatus: ApplicationStatus): ApplicationStatusData
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
    abstract fun map(projectPartnerBudgetOptions: ProjectPartnerBudgetOptions): ProjectPartnerBudgetOptionsData
    abstract fun map(address: Address): WorkPackageInvestmentAddressData
    abstract fun map(workPackageInvestment: WorkPackageInvestment): WorkPackageInvestmentData
    abstract fun map(workPackageInvestments: List<WorkPackageInvestment>): List<WorkPackageInvestmentData>
    abstract fun map(projectHorizontalPrinciplesEffect: ProjectHorizontalPrinciplesEffect): ProjectHorizontalPrinciplesEffectData
    abstract fun map(projectCooperationCriteria: ProjectCooperationCriteria): ProjectCooperationCriteriaData
    abstract fun map(projectManagement: ProjectManagement): ProjectManagementData
    abstract fun map(programmeStrategy: ProgrammeStrategy): ProgrammeStrategyData
    abstract fun map(projectRelevanceStrategy: ProjectRelevanceStrategy): ProjectRelevanceStrategyData
    abstract fun map(projectRelevanceBenefit: ProjectRelevanceBenefit): ProjectRelevanceBenefitData
    abstract fun map(projectRelevance: ProjectRelevance): ProjectRelevanceData
    abstract fun map(outputProgrammePrioritySimple: OutputProgrammePrioritySimple): ProgrammePriorityDataSimple
    abstract fun map(programmeObjectivePolicy: ProgrammeObjectivePolicy): ProgrammeObjectivePolicyData
    abstract fun map(outputProgrammePriorityPolicySimpleDTO: OutputProgrammePriorityPolicySimpleDTO): ProgrammeSpecificObjectiveData

    @Mappings(
        Mapping(target = "coFinancingOverview", source = "tableA3data"),
        Mapping(target = "resultIndicatorOverview", source = "tableA4data")
    )
    abstract fun map(projectFull: ProjectFull, tableA3data: ProjectCoFinancingOverview, tableA4data: ProjectResultIndicatorOverview): ProjectDataSectionA
    abstract fun map(projectHorizontalPrinciples: ProjectHorizontalPrinciples): ProjectHorizontalPrinciplesData
    abstract fun map(programmeStateAidMeasure: ProgrammeStateAidMeasure): ProgrammeStateAidMeasureData
    abstract fun map(workPackageActivitySummary: WorkPackageActivitySummary): WorkPackageActivitySummaryData
    abstract fun map(programmeStateAid: ProgrammeStateAid): ProgrammeStateAidData
    abstract fun map(projectPartnerStateAid: ProjectPartnerStateAid): ProjectPartnerStateAidData
    abstract fun map(projectResult: ProjectResult): ProjectResultData
    abstract fun map(budgetCostsCalculationResult: BudgetCostsCalculationResult): BudgetCostsCalculationResultData
    abstract fun map(projectVersion: ProjectVersion): ProjectVersionData
    abstract fun map(projectPartnerBudgetPerFund: ProjectPartnerBudgetPerFund): ProjectPartnerBudgetPerFundData

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
                investments = pluginDataMapper.map(it.investments)
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

    fun map(projectPartnerData: ProjectPartnerData): ProjectPartnerSummary =
        ProjectPartnerSummary(
            projectPartnerData.id,
            projectPartnerData.abbreviation,
            ProjectPartnerRole.valueOf(projectPartnerData.role.name),
            projectPartnerData.sortNumber,
            projectPartnerData.addresses.firstOrNull { it.type == ProjectPartnerAddressTypeData.Organization }?.country,
            projectPartnerData.addresses.firstOrNull { it.type == ProjectPartnerAddressTypeData.Organization }?.nutsRegion2
        )

}
