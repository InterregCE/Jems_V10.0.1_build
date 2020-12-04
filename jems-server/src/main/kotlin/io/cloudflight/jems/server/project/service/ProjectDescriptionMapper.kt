package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.InputProjectCooperationCriteria
import io.cloudflight.jems.api.project.dto.description.InputProjectHorizontalPrinciples
import io.cloudflight.jems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.InputProjectManagement
import io.cloudflight.jems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.jems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevanceBenefit
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevanceStrategy
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevanceSynergy
import io.cloudflight.jems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.jems.server.project.entity.TranslationId
import io.cloudflight.jems.server.project.entity.TranslationUuId
import io.cloudflight.jems.server.project.entity.description.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.entity.description.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlans
import io.cloudflight.jems.server.project.entity.description.ProjectManagement
import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjective
import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjectiveTransl
import io.cloudflight.jems.server.project.entity.description.ProjectPartnership
import io.cloudflight.jems.server.project.entity.description.ProjectPartnershipTransl
import io.cloudflight.jems.server.project.entity.description.ProjectRelevance
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceBenefitTransl
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceStrategyTransl
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSynergy
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSynergyTransl
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceTransl
import java.util.*
import kotlin.collections.HashSet

// region Project Relevance

fun InputProjectRelevance.toEntity(projectId: Long) =
    ProjectRelevance(
        projectId = projectId,
        translatedValues = combineTranslatedValuesRelevance(
            projectId,
            territorialChallenge,
            commonChallenge,
            transnationalCooperation,
            availableKnowledge
        ),
        projectBenefits = projectBenefits?.mapTo(HashSet()) { it.toEntity() } ?: emptySet(),
        projectStrategies = projectStrategies?.mapTo(HashSet()) { it.toEntity() } ?: emptySet(),
        projectSynergies = projectSynergies?.mapTo(HashSet()) { it.toEntity() } ?: emptySet()
    )

fun combineTranslatedValuesRelevance(
    projectId: Long,
    territorialChallenge: Set<InputTranslation>,
    commonChallenge: Set<InputTranslation>,
    transnationalCooperation: Set<InputTranslation>,
    availableKnowledge: Set<InputTranslation>
): Set<ProjectRelevanceTransl> {
    val territorialChallengeMap = territorialChallenge.associateBy( { it.language }, { it.translation } )
    val commonChallengeMap = commonChallenge.associateBy( { it.language }, { it.translation } )
    val transnationalCooperationMap = transnationalCooperation.associateBy( { it.language }, { it.translation } )
    val availableKnowledgeMap = availableKnowledge.associateBy( { it.language }, { it.translation } )

    val languages = territorialChallengeMap.keys.toMutableSet()
    languages.addAll(commonChallengeMap.keys)
    languages.addAll(transnationalCooperationMap.keys)
    languages.addAll(availableKnowledgeMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectRelevanceTransl(
            TranslationId(projectId, it),
            territorialChallengeMap[it],
            commonChallengeMap[it],
            transnationalCooperationMap[it],
            availableKnowledgeMap[it]
        )
    }
}

fun ProjectRelevance.toOutputProjectRelevance() =
    InputProjectRelevance(
        territorialChallenge = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.territorialChallenge) },
        commonChallenge = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.commonChallenge) },
        transnationalCooperation = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.transnationalCooperation) },
        availableKnowledge = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.availableKnowledge) },
        projectBenefits = projectBenefits.map { it.toOutputProjectBenefit() },
        projectStrategies = projectStrategies.map { it.toOutputProjectStrategy() },
        projectSynergies = projectSynergies.map { it.toOutputProjectSynergy() }
    )
// endregion Project Relevance

// region Project Relevance Benefit

fun InputProjectRelevanceBenefit.toEntity(): ProjectRelevanceBenefit {
    val id = UUID.randomUUID()
    return ProjectRelevanceBenefit(
        id = id,
        targetGroup = group,
        translatedValues = combineTranslatedValuesBenefit(id, specification)
    )
}

fun combineTranslatedValuesBenefit(uuid: UUID, specification: Set<InputTranslation>): MutableSet<ProjectRelevanceBenefitTransl> {
    val specificationMap = specification.associateBy( { it.language }, { it.translation } )
    val languages = specificationMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectRelevanceBenefitTransl(
            TranslationUuId(uuid, it),
            specificationMap[it]
        )
    }
}

fun ProjectRelevanceBenefit.toOutputProjectBenefit() = InputProjectRelevanceBenefit(
    group = targetGroup,
    specification = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.specification) }
)
// endregion Project Relevance Benefit

// region Project Relevance Strategy

fun InputProjectRelevanceStrategy.toEntity(): ProjectRelevanceStrategy {
    val id = UUID.randomUUID()
    return ProjectRelevanceStrategy(
        id = id,
        strategy = strategy,
        translatedValues = combineTranslatedValuesStrategy(id, specification)
    )
}

fun combineTranslatedValuesStrategy(uuid: UUID, specification: Set<InputTranslation>): MutableSet<ProjectRelevanceStrategyTransl> {
    val specificationMap = specification.associateBy( { it.language }, { it.translation } )
    val languages = specificationMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectRelevanceStrategyTransl(
            TranslationUuId(uuid, it),
            specificationMap[it]
        )
    }
}

fun ProjectRelevanceStrategy.toOutputProjectStrategy() = InputProjectRelevanceStrategy(
    strategy = strategy,
    specification = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.specification) }
)
// endregion Project Relevance Strategy

// region Project Relevance Synergy

fun InputProjectRelevanceSynergy.toEntity(): ProjectRelevanceSynergy {
    val id = UUID.randomUUID()
    return ProjectRelevanceSynergy(
        id = id,
        translatedValues = combineTranslatedValuesSynergy(id, synergy, specification)
    )
}

fun combineTranslatedValuesSynergy(
    uuid: UUID,
    synergy: Set<InputTranslation>,
    specification: Set<InputTranslation>
): MutableSet<ProjectRelevanceSynergyTransl> {
    val synergyMap = synergy.associateBy( { it.language }, { it.translation } )
    val specificationMap = specification.associateBy( { it.language }, { it.translation } )

    val languages = synergyMap.keys.toMutableSet()
    languages.addAll(specificationMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectRelevanceSynergyTransl(
            TranslationUuId(uuid, it),
            synergyMap[it],
            specificationMap[it]
        )
    }
}

fun ProjectRelevanceSynergy.toOutputProjectSynergy() = InputProjectRelevanceSynergy(
    synergy = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.synergy) },
    specification = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.specification) }
)
// endregion Project Relevance Synergy

fun InputProjectManagement.toEntity(projectId: Long) =
    ProjectManagement(
        projectId = projectId,
        projectCoordination = projectCoordination,
        projectQualityAssurance = projectQualityAssurance,
        projectCommunication = projectCommunication,
        projectFinancialManagement = projectFinancialManagement,
        projectCooperationCriteria = projectCooperationCriteria?.toEntity(),
        projectHorizontalPrinciples = projectHorizontalPrinciples?.toEntity()
    )

fun ProjectManagement.toOutputProjectManagement() = OutputProjectManagement(
    projectCoordination = projectCoordination,
    projectQualityAssurance = projectQualityAssurance,
    projectCommunication = projectCommunication,
    projectFinancialManagement = projectFinancialManagement,
    projectCooperationCriteria = projectCooperationCriteria?.ifNotEmpty()?.toOutputCooperationCriteria(),
    projectHorizontalPrinciples = projectHorizontalPrinciples?.ifNotEmpty()?.toOutputHorizontalPrinciples()
)

fun InputProjectLongTermPlans.toEntity(projectId: Long) =
    ProjectLongTermPlans(
        projectId = projectId,
        projectOwnership = projectOwnership,
        projectDurability = projectDurability,
        projectTransferability = projectTransferability
    )

fun ProjectLongTermPlans.toOutputProjectLongTermPlans() = OutputProjectLongTermPlans(
    projectOwnership = projectOwnership,
    projectDurability = projectDurability,
    projectTransferability = projectTransferability
)

fun InputProjectHorizontalPrinciples.toEntity() = ProjectHorizontalPrinciples(
    sustainableDevelopmentCriteriaEffect = sustainableDevelopmentCriteriaEffect,
    sustainableDevelopmentDescription = sustainableDevelopmentDescription,
    equalOpportunitiesEffect = equalOpportunitiesEffect,
    equalOpportunitiesDescription = equalOpportunitiesDescription,
    sexualEqualityEffect = sexualEqualityEffect,
    sexualEqualityDescription = sexualEqualityDescription
)

fun ProjectHorizontalPrinciples.toOutputHorizontalPrinciples() = InputProjectHorizontalPrinciples(
    sustainableDevelopmentCriteriaEffect = sustainableDevelopmentCriteriaEffect,
    sustainableDevelopmentDescription = sustainableDevelopmentDescription,
    equalOpportunitiesEffect = equalOpportunitiesEffect,
    equalOpportunitiesDescription = equalOpportunitiesDescription,
    sexualEqualityEffect = sexualEqualityEffect,
    sexualEqualityDescription = sexualEqualityDescription
)

fun InputProjectCooperationCriteria.toEntity() = ProjectCooperationCriteria(
    projectJointDevelopment = projectJointDevelopment,
    projectJointDevelopmentDescription = projectJointDevelopmentDescription,
    projectJointImplementation = projectJointImplementation,
    projectJointImplementationDescription = projectJointImplementationDescription,
    projectJointStaffing = projectJointStaffing,
    projectJointStaffingDescription = projectJointStaffingDescription,
    projectJointFinancing = projectJointFinancing,
    projectJointFinancingDescription = projectJointFinancingDescription
)

fun ProjectCooperationCriteria.toOutputCooperationCriteria() = InputProjectCooperationCriteria(
    projectJointDevelopment = projectJointDevelopment,
    projectJointDevelopmentDescription = projectJointDevelopmentDescription,
    projectJointImplementation = projectJointImplementation,
    projectJointImplementationDescription = projectJointImplementationDescription,
    projectJointStaffing = projectJointStaffing,
    projectJointStaffingDescription = projectJointStaffingDescription,
    projectJointFinancing = projectJointFinancing,
    projectJointFinancingDescription = projectJointFinancingDescription
)

// region Project Overall Objective

fun InputProjectOverallObjective.toEntity(projectId: Long) =
    ProjectOverallObjective(
        projectId = projectId,
        translatedValues = combineTranslatedValuesOverallObjective(
            projectId,
            overallObjective)
    )

fun combineTranslatedValuesOverallObjective(
    projectId: Long,
    overallObjective: Set<InputTranslation>
): Set<ProjectOverallObjectiveTransl> {
    val overallObjectiveMap = overallObjective.associateBy( { it.language }, { it.translation } )

    val languages = overallObjectiveMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectOverallObjectiveTransl(
            TranslationId(projectId, it),
            overallObjectiveMap[it]
        )
    }
}

fun ProjectOverallObjective.toOutputProjectOverallObjective() =
    InputProjectOverallObjective(
        overallObjective = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.overallObjective) }
    )

// end region Project Overall Objective

// region Project Partnership

fun InputProjectPartnership.toEntity(projectId: Long) =
    ProjectPartnership(
        projectId = projectId,
        translatedValues = combineTranslatedValuesPartnership(
            projectId,
            partnership)
    )

fun combineTranslatedValuesPartnership(
    projectId: Long,
    partnership: Set<InputTranslation>
): Set<ProjectPartnershipTransl> {
    val partnershipMap = partnership.associateBy( { it.language }, { it.translation } )

    val languages = partnershipMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectPartnershipTransl(
            TranslationId(projectId, it),
            partnershipMap[it]
        )
    }
}

fun ProjectPartnership.toOutputProjectPartnership() =
    InputProjectPartnership(
        partnership = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.projectPartnership) }
    )

// end region Project Partnership
