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
import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlansTransl
import io.cloudflight.jems.server.project.entity.description.ProjectManagement
import io.cloudflight.jems.server.project.entity.description.ProjectManagementTransl
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
import java.util.UUID
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
        translatedValues = combineTranslatedValuesManagement(
            projectId,
            projectCoordination,
            projectQualityAssurance,
            projectCommunication,
            projectFinancialManagement,
            projectJointDevelopmentDescription,
            projectJointImplementationDescription,
            projectJointStaffingDescription,
            projectJointFinancingDescription,
            sustainableDevelopmentDescription,
            equalOpportunitiesDescription,
            sexualEqualityDescription,
        ),
        projectCooperationCriteria = projectCooperationCriteria?.toEntity(),
        projectHorizontalPrinciples = projectHorizontalPrinciples?.toEntity()
    )

fun combineTranslatedValuesManagement(
    projectId: Long,
    projectCoordination: Set<InputTranslation>,
    projectQualityAssurance: Set<InputTranslation>,
    projectCommunication: Set<InputTranslation>,
    projectFinancialManagement: Set<InputTranslation>,
    projectJointDevelopmentDescription: Set<InputTranslation>,
    projectJointImplementationDescription: Set<InputTranslation>,
    projectJointStaffingDescription: Set<InputTranslation>,
    projectJointFinancingDescription: Set<InputTranslation>,
    sustainableDevelopmentDescription: Set<InputTranslation>,
    equalOpportunitiesDescription: Set<InputTranslation>,
    sexualEqualityDescription: Set<InputTranslation>,
): Set<ProjectManagementTransl> {
    val projectCoordinationMap = projectCoordination.associateBy({ it.language }, { it.translation })
    val projectQualityAssuranceMap = projectQualityAssurance.associateBy({ it.language }, { it.translation })
    val projectCommunicationMap = projectCommunication.associateBy({ it.language }, { it.translation })
    val projectFinancialManagementMap = projectFinancialManagement.associateBy({ it.language }, { it.translation })
    val projectJointDevelopmentDescriptionMap =
        projectJointDevelopmentDescription.associateBy({ it.language }, { it.translation })
    val projectJointImplementationDescriptionMap =
        projectJointImplementationDescription.associateBy({ it.language }, { it.translation })
    val projectJointStaffingDescriptionMap =
        projectJointStaffingDescription.associateBy({ it.language }, { it.translation })
    val projectJointFinancingDescriptionMap =
        projectJointFinancingDescription.associateBy({ it.language }, { it.translation })
    val sustainableDevelopmentDescriptionMap =
        sustainableDevelopmentDescription.associateBy({ it.language }, { it.translation })
    val equalOpportunitiesDescriptionMap =
        equalOpportunitiesDescription.associateBy({ it.language }, { it.translation })
    val sexualEqualityDescriptionMap = sexualEqualityDescription.associateBy({ it.language }, { it.translation })

    val languages = projectCoordinationMap.keys.toMutableSet()
    languages.addAll(projectQualityAssuranceMap.keys)
    languages.addAll(projectCommunicationMap.keys)
    languages.addAll(projectFinancialManagementMap.keys)
    languages.addAll(projectJointDevelopmentDescriptionMap.keys)
    languages.addAll(projectJointImplementationDescriptionMap.keys)
    languages.addAll(projectJointStaffingDescriptionMap.keys)
    languages.addAll(projectJointFinancingDescriptionMap.keys)
    languages.addAll(sustainableDevelopmentDescriptionMap.keys)
    languages.addAll(equalOpportunitiesDescriptionMap.keys)
    languages.addAll(sexualEqualityDescriptionMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectManagementTransl(
            TranslationId(projectId, it),
            projectCoordinationMap[it],
            projectQualityAssuranceMap[it],
            projectCommunicationMap[it],
            projectFinancialManagementMap[it],
            projectJointDevelopmentDescriptionMap[it],
            projectJointImplementationDescriptionMap[it],
            projectJointStaffingDescriptionMap[it],
            projectJointFinancingDescriptionMap[it],
            sustainableDevelopmentDescriptionMap[it],
            equalOpportunitiesDescriptionMap[it],
            sexualEqualityDescriptionMap[it],
        )
    }
}

fun ProjectManagement.toOutputProjectManagement() = OutputProjectManagement(
    projectCoordination = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectCoordination)
    },
    projectQualityAssurance = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectQualityAssurance)
    },
    projectCommunication = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectCommunication)
    },
    projectFinancialManagement = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectFinancialManagement)
    },
    projectCooperationCriteria = projectCooperationCriteria?.ifNotEmpty()?.toOutputCooperationCriteria(),
    projectJointDevelopmentDescription = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectJointDevelopmentDescription)
    },
    projectJointImplementationDescription = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectJointImplementationDescription)
    },
    projectJointStaffingDescription = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectJointStaffingDescription)
    },
    projectJointFinancingDescription = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectJointFinancingDescription)
    },
    projectHorizontalPrinciples = projectHorizontalPrinciples?.ifNotEmpty()?.toOutputHorizontalPrinciples(),
    sustainableDevelopmentDescription = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.sustainableDevelopmentDescription)
    },
    equalOpportunitiesDescription = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.equalOpportunitiesDescription)
    },
    sexualEqualityDescription = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.sexualEqualityDescription)
    },
)

fun InputProjectLongTermPlans.toEntity(projectId: Long) =
    ProjectLongTermPlans(
        projectId = projectId,
        translatedValues = combineTranslatedValuesLongTermPlans(
            projectId,
            projectOwnership,
            projectDurability,
            projectTransferability
        )
    )

fun combineTranslatedValuesLongTermPlans(
    projectId: Long,
    projectOwnership: Set<InputTranslation>,
    projectDurability: Set<InputTranslation>,
    projectTransferability: Set<InputTranslation>
): Set<ProjectLongTermPlansTransl> {
    val projectOwnershipMap = projectOwnership.associateBy({ it.language }, { it.translation })
    val projectDurabilityMap = projectDurability.associateBy({ it.language }, { it.translation })
    val projectTransferabilityMap = projectTransferability.associateBy({ it.language }, { it.translation })

    val languages = projectOwnershipMap.keys.toMutableSet()
    languages.addAll(projectDurabilityMap.keys)
    languages.addAll(projectTransferabilityMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectLongTermPlansTransl(
            TranslationId(projectId, it),
            projectOwnershipMap[it],
            projectDurabilityMap[it],
            projectTransferabilityMap[it]
        )
    }
}

fun ProjectLongTermPlans.toOutputProjectLongTermPlans() = OutputProjectLongTermPlans(
    projectOwnership = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectOwnership)
    },
    projectDurability = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectDurability)
    },
    projectTransferability = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.projectTransferability)
    },
)

fun InputProjectHorizontalPrinciples.toEntity() = ProjectHorizontalPrinciples(
    sustainableDevelopmentCriteriaEffect = sustainableDevelopmentCriteriaEffect,
    equalOpportunitiesEffect = equalOpportunitiesEffect,
    sexualEqualityEffect = sexualEqualityEffect
)

fun ProjectHorizontalPrinciples.toOutputHorizontalPrinciples() = InputProjectHorizontalPrinciples(
    sustainableDevelopmentCriteriaEffect = sustainableDevelopmentCriteriaEffect,
    equalOpportunitiesEffect = equalOpportunitiesEffect,
    sexualEqualityEffect = sexualEqualityEffect
)

fun InputProjectCooperationCriteria.toEntity() = ProjectCooperationCriteria(
    projectJointDevelopment = projectJointDevelopment,
    projectJointImplementation = projectJointImplementation,
    projectJointStaffing = projectJointStaffing,
    projectJointFinancing = projectJointFinancing
)

fun ProjectCooperationCriteria.toOutputCooperationCriteria() = InputProjectCooperationCriteria(
    projectJointDevelopment = projectJointDevelopment,
    projectJointImplementation = projectJointImplementation,
    projectJointStaffing = projectJointStaffing,
    projectJointFinancing = projectJointFinancing
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
