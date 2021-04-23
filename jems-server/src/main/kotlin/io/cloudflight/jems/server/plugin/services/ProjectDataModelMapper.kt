package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.api.project.dto.ProjectDataDTO
import io.cloudflight.jems.api.project.dto.description.InputProjectCooperationCriteria
import io.cloudflight.jems.api.project.dto.description.InputProjectHorizontalPrinciples
import io.cloudflight.jems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.jems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.jems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.jems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammeObjectivePolicyData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammePriorityDataSimple
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammeSpecificObjectiveData
import io.cloudflight.jems.plugin.contract.models.programme.strategy.ProgrammeStrategyData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.ProjectDataSectionA
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
import io.cloudflight.jems.plugin.contract.models.project.sectionC.results.ProjectResultTranslatedValueData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.ProjectWorkPackageData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.ProjectWorkPackageTranslatedValueData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityDeliverableData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityDeliverableTranslatedValueData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityTranslatedValueData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageOutputData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageOutputTranslatedValueData
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput

fun ProjectDataDTO.toDataModel() = ProjectDataSectionA(
    title = title.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
    intro = intro.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
    duration = duration,
    specificObjective = specificObjective?.toDataModel(),
    programmePriority = programmePriority?.toDataModel()
)

fun OutputProgrammePriorityPolicySimpleDTO.toDataModel() = ProgrammeSpecificObjectiveData(
    code = code,
    programmeObjectivePolicy = ProgrammeObjectivePolicyData.valueOf(programmeObjectivePolicy.name),
)

fun OutputProgrammePrioritySimple.toDataModel() = ProgrammePriorityDataSimple(
    code = code,
    title = title.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet()
)

fun OutputProjectDescription.toDataModel(workPackages: List<ProjectWorkPackageData>, results: List<ProjectResultData>) = ProjectDataSectionC(
    projectOverallObjective = projectOverallObjective?.toDataModel(),
    projectRelevance = projectRelevance?.toDataModel(),
    projectPartnership = projectPartnership?.toDataModel(),
    projectWorkPackages = workPackages,
    projectResults = results,
    projectManagement = projectManagement?.toDataModel(),
    projectLongTermPlans = projectLongTermPlans?.toDataModel()
)

fun InputProjectOverallObjective.toDataModel() = ProjectOverallObjectiveData(
    overallObjective = overallObjective.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet()
)

fun InputProjectRelevance.toDataModel() = ProjectRelevanceData(
    territorialChallenge = territorialChallenge.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
    commonChallenge = commonChallenge.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
    transnationalCooperation = transnationalCooperation.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
    projectBenefits = projectBenefits?.map { ProjectRelevanceBenefitData(
        group = ProjectTargetGroupData.valueOf(it.group.name),
        specification = it.specification.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet()
    ) }?.toList(),
    projectStrategies = projectStrategies?.map { ProjectRelevanceStrategyData(
        strategy = ProgrammeStrategyData.valueOf(it.strategy!!.name),
        specification = it.specification.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet()
    ) }?.toList(),
    projectSynergies = projectSynergies?.map { ProjectRelevanceSynergyData(
        synergy = it.synergy.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
        specification = it.specification.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
    ) }?.toList(),
    availableKnowledge = availableKnowledge.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
)

fun InputProjectPartnership.toDataModel() = ProjectPartnershipData(
    partnership = partnership.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
)

fun OutputProjectManagement.toDataModel() = ProjectManagementData(
    projectCoordination = projectCoordination?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet(),
    projectQualityAssurance = projectQualityAssurance?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet(),
    projectCommunication = projectCommunication?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet(),
    projectFinancialManagement = projectFinancialManagement?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet(),
    projectCooperationCriteria = projectCooperationCriteria?.toDataModel(),
    projectJointDevelopmentDescription = projectJointDevelopmentDescription?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet(),
    projectJointImplementationDescription = projectJointImplementationDescription?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet(),
    projectJointStaffingDescription = projectJointStaffingDescription?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet(),
    projectJointFinancingDescription = projectJointFinancingDescription?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet(),
    projectHorizontalPrinciples = projectHorizontalPrinciples?.toDataModel(),
    sustainableDevelopmentDescription = sustainableDevelopmentDescription?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet(),
    equalOpportunitiesDescription = equalOpportunitiesDescription?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet(),
    sexualEqualityDescription = sexualEqualityDescription?.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }?.toSet()
)

fun InputProjectCooperationCriteria.toDataModel() = ProjectCooperationCriteriaData(
    projectJointDevelopment = projectJointDevelopment,
    projectJointImplementation = projectJointImplementation,
    projectJointStaffing = projectJointStaffing,
    projectJointFinancing = projectJointFinancing
)

fun InputProjectHorizontalPrinciples.toDataModel() = ProjectHorizontalPrinciplesData(
    sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffectData.valueOf(sustainableDevelopmentCriteriaEffect!!.name),
    equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffectData.valueOf(equalOpportunitiesEffect!!.name),
    sexualEqualityEffect = ProjectHorizontalPrinciplesEffectData.valueOf(sexualEqualityEffect!!.name)
)

fun OutputProjectLongTermPlans.toDataModel() = ProjectLongTermPlansData(
    projectOwnership = projectOwnership.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
    projectDurability = projectDurability.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
    projectTransferability = projectTransferability.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet()
)

fun List<ProjectWorkPackage>.toDataModel() = map {
    ProjectWorkPackageData(
        id = it.id,
        workPackageNumber = it.workPackageNumber,
        translatedValues = it.translatedValues.map { ProjectWorkPackageTranslatedValueData(language = SystemLanguageData.valueOf(it.language.name), name = it.name) }.toSet(),
        activities = it.activities.toActivityDataModel(),
        outputs = it.outputs.toOutputDataModel()
) }.toList()

fun List<WorkPackageActivity>.toActivityDataModel() = map {
    WorkPackageActivityData(
        activityNumber = it.activityNumber,
        translatedValues = it.translatedValues.map { WorkPackageActivityTranslatedValueData(language = SystemLanguageData.valueOf(it.language.name), title = it.title, description = it.description) }.toSet(),
        startPeriod = it.startPeriod,
        endPeriod = it.endPeriod,
        deliverables = it.deliverables.toDeliverableDataModel()
    )
}.toList()

fun List<WorkPackageActivityDeliverable>.toDeliverableDataModel() = map {
    WorkPackageActivityDeliverableData(
        deliverableNumber = it.deliverableNumber,
        translatedValues = it.translatedValues.map { WorkPackageActivityDeliverableTranslatedValueData(language = SystemLanguageData.valueOf(it.language.name), description = it.description) }.toSet(),
        period = it.period
    )
}.toList()

fun List<WorkPackageOutput>.toOutputDataModel() = map {
    WorkPackageOutputData(
        outputNumber = it.outputNumber,
        programmeOutputIndicatorId = it.programmeOutputIndicatorId,
        programmeOutputIndicatorIdentifier = it.programmeOutputIndicatorIdentifier,
        targetValue = it.targetValue,
        translatedValues = it.translatedValues.map { WorkPackageOutputTranslatedValueData(language = SystemLanguageData.valueOf(it.language.name), title = it.title, description = it.description) }.toSet(),
    )
}.toList()

fun List<ProjectResult>.toResultDataModel() = map {
    ProjectResultData(
        resultNumber = it.resultNumber,
        programmeResultIndicatorId = it.programmeResultIndicatorId,
        programmeResultIndicatorIdentifier = it.programmeResultIndicatorIdentifier,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        translatedValues = it.translatedValues.map { ProjectResultTranslatedValueData(language = SystemLanguageData.valueOf(it.language.name), description = it.description) }.toSet(),
    )
}.toList()