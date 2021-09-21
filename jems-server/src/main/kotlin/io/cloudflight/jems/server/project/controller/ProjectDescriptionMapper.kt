package io.cloudflight.jems.server.project.controller

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
import io.cloudflight.jems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.jems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.service.model.ProjectDescription
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectRelevance
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceSynergy

// map for get (model) to DTOs for API

fun ProjectOverallObjective.toDto() = InputProjectOverallObjective(
    overallObjective = overallObjective
)
fun ProjectRelevance.toDto() = InputProjectRelevance(
    territorialChallenge = territorialChallenge,
    commonChallenge = commonChallenge,
    transnationalCooperation = transnationalCooperation,
    availableKnowledge = availableKnowledge,
    projectBenefits = projectBenefits?.map { it.toProjectBenefit() },
    projectStrategies = projectStrategies?.map { it.toProjectStrategy() },
    projectSynergies = projectSynergies?.map { it.toProjectSynergy() }
)
fun ProjectRelevanceBenefit.toProjectBenefit() = InputProjectRelevanceBenefit(
    group = group,
    specification = specification
)
fun ProjectRelevanceStrategy.toProjectStrategy() = InputProjectRelevanceStrategy(
    strategy = strategy,
    specification = specification
)
fun ProjectRelevanceSynergy.toProjectSynergy() = InputProjectRelevanceSynergy(
    synergy = synergy,
    specification = specification
)
fun ProjectPartnership.toDto() = InputProjectPartnership(
    partnership = partnership
)
fun ProjectManagement.toDto() = OutputProjectManagement(
    projectCoordination = projectCoordination ?: emptySet(),
    projectQualityAssurance = projectQualityAssurance ?: emptySet(),
    projectCommunication = projectCommunication ?: emptySet(),
    projectFinancialManagement = projectFinancialManagement ?: emptySet(),
    projectCooperationCriteria = projectCooperationCriteria?.toDto(),
    projectJointDevelopmentDescription = projectJointDevelopmentDescription ?: emptySet(),
    projectJointImplementationDescription = projectJointImplementationDescription ?: emptySet(),
    projectJointStaffingDescription = projectJointStaffingDescription ?: emptySet(),
    projectJointFinancingDescription = projectJointFinancingDescription ?: emptySet(),
    projectHorizontalPrinciples = projectHorizontalPrinciples?.toDto(),
    sustainableDevelopmentDescription = sustainableDevelopmentDescription ?: emptySet(),
    equalOpportunitiesDescription = equalOpportunitiesDescription ?: emptySet(),
    sexualEqualityDescription = sexualEqualityDescription ?: emptySet()
)
fun ProjectCooperationCriteria.toDto() = InputProjectCooperationCriteria(
    projectJointDevelopment = projectJointDevelopment,
    projectJointImplementation = projectJointImplementation,
    projectJointStaffing = projectJointStaffing,
    projectJointFinancing = projectJointFinancing
)
fun ProjectHorizontalPrinciples.toDto() = InputProjectHorizontalPrinciples(
    sustainableDevelopmentCriteriaEffect = sustainableDevelopmentCriteriaEffect,
    equalOpportunitiesEffect = equalOpportunitiesEffect,
    sexualEqualityEffect = sexualEqualityEffect
)
fun ProjectLongTermPlans.toDto() = OutputProjectLongTermPlans(
    projectOwnership = projectOwnership,
    projectDurability = projectDurability,
    projectTransferability = projectTransferability
)

fun ProjectDescription.toDto() = OutputProjectDescription(
    projectOverallObjective = projectOverallObjective?.toDto(),
    projectRelevance = projectRelevance?.toDto(),
    projectPartnership = projectPartnership?.toDto(),
    projectManagement = projectManagement?.toDto(),
    projectLongTermPlans = projectLongTermPlans?.toDto()
)

// map for update (input DTOs) to model

fun InputProjectOverallObjective.toModel() =
    ProjectOverallObjective(
        overallObjective = overallObjective
    )
fun InputProjectRelevance.toModel() =
    ProjectRelevance(
        territorialChallenge = territorialChallenge,
        commonChallenge = commonChallenge,
        transnationalCooperation = transnationalCooperation,
        availableKnowledge = availableKnowledge,
        projectBenefits = projectBenefits?.map { it.toProjectBenefit() },
        projectStrategies = projectStrategies?.map { it.toProjectStrategy() },
        projectSynergies = projectSynergies?.map { it.toProjectSynergy() }
    )
fun InputProjectRelevanceBenefit.toProjectBenefit() = ProjectRelevanceBenefit(
    group = group,
    specification = specification
)
fun InputProjectRelevanceStrategy.toProjectStrategy() = ProjectRelevanceStrategy(
    strategy = strategy,
    specification = specification
)
fun InputProjectRelevanceSynergy.toProjectSynergy() = ProjectRelevanceSynergy(
    synergy = synergy,
    specification = specification
)
fun InputProjectPartnership.toModel() =
    ProjectPartnership(
        partnership = partnership
    )
fun InputProjectManagement.toModel() = ProjectManagement(
    projectCoordination = projectCoordination,
    projectQualityAssurance = projectQualityAssurance,
    projectCommunication = projectCommunication,
    projectFinancialManagement = projectFinancialManagement,
    projectCooperationCriteria = projectCooperationCriteria?.toModel(),
    projectJointDevelopmentDescription = projectJointDevelopmentDescription,
    projectJointImplementationDescription = projectJointImplementationDescription,
    projectJointStaffingDescription = projectJointStaffingDescription,
    projectJointFinancingDescription = projectJointFinancingDescription,
    projectHorizontalPrinciples = projectHorizontalPrinciples?.toModel(),
    sustainableDevelopmentDescription = sustainableDevelopmentDescription,
    equalOpportunitiesDescription = equalOpportunitiesDescription,
    sexualEqualityDescription = sexualEqualityDescription
)
fun InputProjectCooperationCriteria.toModel() = ProjectCooperationCriteria(
    projectJointDevelopment = projectJointDevelopment,
    projectJointImplementation = projectJointImplementation,
    projectJointStaffing = projectJointStaffing,
    projectJointFinancing = projectJointFinancing
)
fun InputProjectHorizontalPrinciples.toModel() = ProjectHorizontalPrinciples(
    sustainableDevelopmentCriteriaEffect = sustainableDevelopmentCriteriaEffect,
    equalOpportunitiesEffect = equalOpportunitiesEffect,
    sexualEqualityEffect = sexualEqualityEffect
)
fun InputProjectLongTermPlans.toModel() = ProjectLongTermPlans(
    projectOwnership = projectOwnership,
    projectDurability = projectDurability,
    projectTransferability = projectTransferability
)
