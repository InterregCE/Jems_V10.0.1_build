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
import io.cloudflight.jems.server.project.entity.description.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.entity.description.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlans
import io.cloudflight.jems.server.project.entity.description.ProjectManagement
import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjective
import io.cloudflight.jems.server.project.entity.description.ProjectPartnership
import io.cloudflight.jems.server.project.entity.description.ProjectRelevance
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSynergy
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceTransl

// region Project Relevance

fun InputProjectRelevance.toEntity(projectId: Long) =
    ProjectRelevance(
        projectId = projectId,
        translatedValues = combineTranslatedValues(projectId, territorialChallenge, commonChallenge, transnationalCooperation),
        projectBenefits = projectBenefits?.mapTo(HashSet()) { it.toEntity() } ?: emptySet(),
        projectStrategies = projectStrategies?.mapTo(HashSet()) { it.toEntity() } ?: emptySet(),
        projectSynergies = projectSynergies?.mapTo(HashSet()) { it.toEntity() } ?: emptySet(),
        availableKnowledge = availableKnowledge
    )

fun combineTranslatedValues(
    projectId: Long,
    territorialChallenge: Set<InputTranslation>,
    commonChallenge: Set<InputTranslation>,
    transnationalCooperation: Set<InputTranslation>
): Set<ProjectRelevanceTransl> {
    val territorialChallengeMap = territorialChallenge.associateBy( { it.language }, { it.translation } )
    val commonChallengeMap = commonChallenge.associateBy( { it.language }, { it.translation } )
    val transnationalCooperationMap = transnationalCooperation.associateBy( { it.language }, { it.translation } )

    val languages = territorialChallengeMap.keys.toMutableSet()
    languages.addAll(commonChallengeMap.keys)
    languages.addAll(transnationalCooperationMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectRelevanceTransl(
            TranslationId(projectId, it),
            territorialChallengeMap[it],
            commonChallengeMap[it],
            transnationalCooperationMap[it]
        )
    }
}

fun ProjectRelevance.toOutputProjectRelevance() =
    InputProjectRelevance(
        territorialChallenge = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.territorialChallenge) },
        commonChallenge = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.commonChallenge) },
        transnationalCooperation = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.transnationalCooperation) },
        projectBenefits = projectBenefits.map { it.toOutputProjectBenefit() },
        projectStrategies = projectStrategies.map { it.toOutputProjectStrategy() },
        projectSynergies = projectSynergies.map { it.toOutputProjectSynergy() },
        availableKnowledge = availableKnowledge
    )
// endregion Project Relevance

// region Project Relevance Benefit

fun InputProjectRelevanceBenefit.toEntity() = ProjectRelevanceBenefit(
    targetGroup = group,
    specification = specification
)

fun ProjectRelevanceBenefit.toOutputProjectBenefit() = InputProjectRelevanceBenefit(
    group = targetGroup,
    specification = specification
)
// endregion Project Relevance Benefit

// region Project Relevance Strategy

fun InputProjectRelevanceStrategy.toEntity() = ProjectRelevanceStrategy(
    strategy = strategy,
    specification = specification
)

fun ProjectRelevanceStrategy.toOutputProjectStrategy() = InputProjectRelevanceStrategy(
    strategy = strategy,
    specification = specification
)
// endregion Project Relevance Strategy

// region Project Relevance Synergy

fun InputProjectRelevanceSynergy.toEntity() = ProjectRelevanceSynergy(
    synergy = synergy,
    specification = specification
)

fun ProjectRelevanceSynergy.toOutputProjectSynergy() = InputProjectRelevanceSynergy(
    synergy = synergy,
    specification = specification
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

fun InputProjectOverallObjective.toEntity(projectId: Long) =
    ProjectOverallObjective(
        projectId = projectId,
        projectOverallObjective = overallObjective
    )

fun ProjectOverallObjective.toOutputProjectOverallObjective() =
    InputProjectOverallObjective(
        overallObjective = projectOverallObjective
    )

fun InputProjectPartnership.toEntity(projectId: Long) =
    ProjectPartnership(
        projectId = projectId,
        projectPartnership = partnership
    )

fun ProjectPartnership.toOutputProjectPartnership() =
    InputProjectPartnership(
        partnership = projectPartnership
    )
