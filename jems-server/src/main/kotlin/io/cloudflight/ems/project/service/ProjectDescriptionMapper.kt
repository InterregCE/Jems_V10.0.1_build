package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.description.InputProjectHorizontalPrinciples
import io.cloudflight.ems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.InputProjectManagement
import io.cloudflight.ems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevanceBenefit
import io.cloudflight.ems.api.project.dto.description.InputProjectCooperationCriteria
import io.cloudflight.ems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.ems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevanceStrategy
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevanceSynergy
import io.cloudflight.ems.project.entity.description.ProjectHorizontalPrinciples
import io.cloudflight.ems.project.entity.description.ProjectLongTermPlans
import io.cloudflight.ems.project.entity.description.ProjectManagement
import io.cloudflight.ems.project.entity.description.ProjectRelevanceBenefit
import io.cloudflight.ems.project.entity.description.ProjectCooperationCriteria
import io.cloudflight.ems.project.entity.description.ProjectOverallObjective
import io.cloudflight.ems.project.entity.description.ProjectPartnership
import io.cloudflight.ems.project.entity.description.ProjectRelevance
import io.cloudflight.ems.project.entity.description.ProjectRelevanceStrategy
import io.cloudflight.ems.project.entity.description.ProjectRelevanceSynergy
import kotlin.collections.HashSet

// region Project Relevance

fun InputProjectRelevance.toEntity(projectId: Long) =
    ProjectRelevance(
        projectId = projectId,
        territorialChallenge = territorialChallenge,
        commonChallenge = commonChallenge,
        transnationalCooperation = transnationalCooperation,
        projectBenefits = projectBenefits?.mapTo(HashSet()) { it.toEntity() } ?: emptySet(),
        projectStrategies = projectStrategies?.mapTo(HashSet()) { it.toEntity() } ?: emptySet(),
        projectSynergies = projectSynergies?.mapTo(HashSet()) { it.toEntity() } ?: emptySet(),
        availableKnowledge = availableKnowledge
    )

fun ProjectRelevance.toOutputProjectRelevance() =
    InputProjectRelevance(
        territorialChallenge = territorialChallenge,
        commonChallenge = commonChallenge,
        transnationalCooperation = transnationalCooperation,
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
