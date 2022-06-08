package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.controller.toModel
import io.cloudflight.jems.server.project.entity.TranslationId
import io.cloudflight.jems.server.project.entity.description.ProjectCooperationCriteriaEntity
import io.cloudflight.jems.server.project.entity.description.ProjectHorizontalPrinciplesEntity
import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlansEntity
import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlansRow
import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlansTransl
import io.cloudflight.jems.server.project.entity.description.ProjectManagementEntity
import io.cloudflight.jems.server.project.entity.description.ProjectManagementRow
import io.cloudflight.jems.server.project.entity.description.ProjectManagementTransl
import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjectiveEntity
import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjectiveRow
import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjectiveTransl
import io.cloudflight.jems.server.project.entity.description.ProjectPartnershipEntity
import io.cloudflight.jems.server.project.entity.description.ProjectPartnershipRow
import io.cloudflight.jems.server.project.entity.description.ProjectPartnershipTransl
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceBenefitEntity
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceBenefitRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceBenefitTransl
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceEntity
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSpfRecipientEntity
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSpfRecipientRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSpfRecipientTransl
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceStrategyEntity
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceStrategyRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceStrategyTransl
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSynergyEntity
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSynergyRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSynergyTransl
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceTransl
import io.cloudflight.jems.server.project.service.model.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectRelevance
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceSpfRecipient
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceSynergy
import java.util.UUID

// region Project Relevance

fun ProjectRelevance.toEntity(projectId: Long) =
    ProjectRelevanceEntity(
        projectId = projectId,
        translatedValues = combineTranslatedValuesRelevance(
            projectId,
            territorialChallenge,
            commonChallenge,
            transnationalCooperation,
            availableKnowledge
        ),
        projectBenefits = projectBenefits?.mapIndexedTo(HashSet()) { index, it -> it.toEntity(index + 1) } ?: emptySet(),
        projectSpfRecipients = projectSpfRecipients?.mapIndexedTo(HashSet()) { index, it -> it.toEntity(index + 1) } ?: emptySet(),
        projectStrategies = projectStrategies?.mapIndexedTo(HashSet()) { index, it -> it.toEntity(index + 1) } ?: emptySet(),
        projectSynergies = projectSynergies?.mapIndexedTo(HashSet()) { index, it -> it.toEntity(index + 1) } ?: emptySet()
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

fun List<ProjectRelevanceRow>.toProjectRelevance(
    projectBenefits: List<ProjectRelevanceBenefit>,
    projectSpfRecipients: List<ProjectRelevanceSpfRecipient>,
    projectStrategies: List<ProjectRelevanceStrategy>,
    projectSynergies: List<ProjectRelevanceSynergy>
) = ProjectRelevance(
    territorialChallenge = extractField { it.territorialChallenge } ,
    commonChallenge = extractField { it.commonChallenge } ,
    transnationalCooperation = extractField { it.transnationalCooperation },
    projectBenefits = projectBenefits,
    projectSpfRecipients = projectSpfRecipients,
    projectStrategies = projectStrategies,
    projectSynergies = projectSynergies,
    availableKnowledge = extractField { it.availableKnowledge }
)

fun List<ProjectRelevanceBenefitRow>.toRelevanceBenefits() =
    this.groupBy { it.id }.map { groupedRows ->
        ProjectRelevanceBenefit(
            group = groupedRows.value.first().targetGroup,
            specification = groupedRows.value.extractField { it.specification }
        )
    }

fun List<ProjectRelevanceSpfRecipientRow>.toRelevanceSpfRecipients() =
    this.groupBy { it.id }.map { groupedRows ->
        ProjectRelevanceSpfRecipient(
            recipientGroup = groupedRows.value.first().recipientGroup.toModel(),
            specification = groupedRows.value.extractField { it.specification }
        )
    }

fun List<ProjectRelevanceStrategyRow>.toRelevanceStrategies() =
    this.groupBy { it.id }.map { groupedRows ->
        ProjectRelevanceStrategy(
            strategy = getMappedStrategy(groupedRows.value.first().strategy),
            specification = groupedRows.value.extractField { it.specification }
        )
    }
fun List<ProjectRelevanceSynergyRow>.toRelevanceSynergies() =
    this.groupBy { it.id }.map { groupedRows ->
        ProjectRelevanceSynergy(
            synergy = groupedRows.value.extractField { it.synergy },
            specification = groupedRows.value.extractField { it.specification }
        )
    }

fun ProjectRelevanceEntity.toProjectRelevance() =
    ProjectRelevance(
        territorialChallenge = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.territorialChallenge) },
        commonChallenge = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.commonChallenge) },
        transnationalCooperation = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.transnationalCooperation) },
        availableKnowledge = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.availableKnowledge) },
        projectBenefits = projectBenefits.sortedBy { it.sortNumber }.map { it.toProjectBenefit() },
        projectSpfRecipients = projectSpfRecipients.sortedBy { it.sortNumber }.map { it.toProjectSpfRecipient() },
        projectStrategies = projectStrategies.sortedBy { it.sortNumber }.map { it.toProjectStrategy() },
        projectSynergies = projectSynergies.sortedBy { it.sortNumber }.map { it.toProjectSynergy() },
    )
// endregion Project Relevance

// region Project Relevance Benefit

fun ProjectRelevanceBenefit.toEntity(sortNumber: Int): ProjectRelevanceBenefitEntity {
    return ProjectRelevanceBenefitEntity(
        id = UUID.randomUUID(),
        sortNumber = sortNumber,
        targetGroup = group,
    ).apply {
        translatedValues.addAll(combineTranslatedValuesBenefit(this, specification))
    }
}
fun ProjectRelevanceSpfRecipient.toEntity(sortNumber: Int): ProjectRelevanceSpfRecipientEntity {
    return ProjectRelevanceSpfRecipientEntity(
        id = UUID.randomUUID(),
        sortNumber = sortNumber,
        recipientGroup = recipientGroup,
    ).apply {
        translatedValues.addAll(combineTranslatedValuesSpfRecipient(this, specification))
    }
}

fun combineTranslatedValuesBenefit(relevanceBenefit: ProjectRelevanceBenefitEntity, specification: Set<InputTranslation>): MutableSet<ProjectRelevanceBenefitTransl> {
    val specificationMap = specification.associateBy( { it.language }, { it.translation } )
    val languages = specificationMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectRelevanceBenefitTransl(
            io.cloudflight.jems.server.common.entity.TranslationId(relevanceBenefit, it),
            specificationMap[it]
        )
    }
}

fun combineTranslatedValuesSpfRecipient(relevanceSpfRecipient: ProjectRelevanceSpfRecipientEntity, specification: Set<InputTranslation>): MutableSet<ProjectRelevanceSpfRecipientTransl> {
    val specificationMap = specification.associateBy( { it.language }, { it.translation } )
    val languages = specificationMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectRelevanceSpfRecipientTransl(
            io.cloudflight.jems.server.common.entity.TranslationId(relevanceSpfRecipient, it),
            specificationMap[it]
        )
    }
}

fun ProjectRelevanceBenefitEntity.toProjectBenefit() = ProjectRelevanceBenefit(
    group = targetGroup,
    specification = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.specification) }
)

fun ProjectRelevanceSpfRecipientEntity.toProjectSpfRecipient() = ProjectRelevanceSpfRecipient(
    recipientGroup = recipientGroup,
    specification = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.specification) }
)
// endregion Project Relevance Benefit

// region Project Relevance Strategy

fun ProjectRelevanceStrategy.toEntity(sortNumber: Int): ProjectRelevanceStrategyEntity {
    return ProjectRelevanceStrategyEntity(
        id = UUID.randomUUID(),
        sortNumber = sortNumber,
        strategy = getMappedStrategy(strategy),
    ).apply {
        translatedValues.addAll(combineTranslatedValuesStrategy(this, specification))
    }
}

fun getMappedStrategy(strategy: ProgrammeStrategy?): ProgrammeStrategy? {
    if (strategy == ProgrammeStrategy.SeaBasinStrategyAdriaticIonianSea) {
        return ProgrammeStrategy.EUStrategyAdriaticIonianRegion
    }

    if (strategy == ProgrammeStrategy.SeaBasinStrategyBalticSea) {
        return ProgrammeStrategy.EUStrategyBalticSeaRegion
    }

    return strategy
}

fun combineTranslatedValuesStrategy(relevanceStrategy: ProjectRelevanceStrategyEntity, specification: Set<InputTranslation>): MutableSet<ProjectRelevanceStrategyTransl> {
    val specificationMap = specification.associateBy( { it.language }, { it.translation } )
    val languages = specificationMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectRelevanceStrategyTransl(
            io.cloudflight.jems.server.common.entity.TranslationId(relevanceStrategy, it),
            specificationMap[it]
        )
    }
}

fun ProjectRelevanceStrategyEntity.toProjectStrategy() = ProjectRelevanceStrategy(
    strategy = getMappedStrategy(strategy),
    specification = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.specification) }
)
// endregion Project Relevance Strategy

// region Project Relevance Synergy

fun ProjectRelevanceSynergy.toEntity(sortNumber: Int): ProjectRelevanceSynergyEntity {
    return ProjectRelevanceSynergyEntity(
        id =  UUID.randomUUID(),
        sortNumber = sortNumber,
    ).apply {
        translatedValues.addAll(combineTranslatedValuesSynergy(this, synergy, specification))
    }
}

fun combineTranslatedValuesSynergy(
    relevanceSynergy: ProjectRelevanceSynergyEntity,
    synergy: Set<InputTranslation>,
    specification: Set<InputTranslation>
): MutableSet<ProjectRelevanceSynergyTransl> {
    val synergyMap = synergy.associateBy( { it.language }, { it.translation } )
    val specificationMap = specification.associateBy( { it.language }, { it.translation } )

    val languages = synergyMap.keys.toMutableSet()
    languages.addAll(specificationMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectRelevanceSynergyTransl(
            io.cloudflight.jems.server.common.entity.TranslationId(relevanceSynergy, it),
            synergyMap[it],
            specificationMap[it]
        )
    }
}

fun ProjectRelevanceSynergyEntity.toProjectSynergy() = ProjectRelevanceSynergy(
    synergy = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.synergy) },
    specification = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.specification) }
)
// endregion Project Relevance Synergy

fun ProjectManagement.toEntity(projectId: Long) =
    ProjectManagementEntity(
        projectId = projectId,
        translatedValues = combineTranslatedValuesManagement(
            projectId,
            projectCoordination ?: emptySet(),
            projectQualityAssurance ?: emptySet(),
            projectCommunication ?: emptySet(),
            projectFinancialManagement ?: emptySet(),
            projectJointDevelopmentDescription ?: emptySet(),
            projectJointImplementationDescription ?: emptySet(),
            projectJointStaffingDescription ?: emptySet(),
            projectJointFinancingDescription ?: emptySet(),
            sustainableDevelopmentDescription ?: emptySet(),
            equalOpportunitiesDescription ?: emptySet(),
            sexualEqualityDescription ?: emptySet()
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

fun List<ProjectManagementRow>.toProjectManagement() =
    ProjectManagement(
        projectCoordination = extractField { it.projectCoordination },
        projectQualityAssurance = extractField { it.projectQualityAssurance },
        projectCommunication = extractField { it.projectCommunication },
        projectFinancialManagement = extractField { it.projectFinancialManagement },
        projectCooperationCriteria = ProjectCooperationCriteria(
            projectJointDevelopment = firstOrNull()?.projectJointDevelopment ?: false,
            projectJointImplementation = firstOrNull()?.projectJointImplementation ?: false,
            projectJointStaffing = firstOrNull()?.projectJointStaffing ?: false,
            projectJointFinancing = firstOrNull()?.projectJointFinancing ?: false
        ),
        projectJointDevelopmentDescription = extractField { it.projectJointDevelopmentDescription },
        projectJointImplementationDescription = extractField { it.projectJointImplementationDescription },
        projectJointStaffingDescription = extractField { it.projectJointStaffingDescription },
        projectJointFinancingDescription = extractField { it.projectJointFinancingDescription },
        projectHorizontalPrinciples = ProjectHorizontalPrinciples(
            sustainableDevelopmentCriteriaEffect = firstOrNull()?.sustainableDevelopmentCriteriaEffect,
            equalOpportunitiesEffect = firstOrNull()?.equalOpportunitiesEffect,
            sexualEqualityEffect = firstOrNull()?.sexualEqualityEffect
        ),
        sustainableDevelopmentDescription = extractField { it.sustainableDevelopmentDescription },
        equalOpportunitiesDescription = extractField { it.equalOpportunitiesDescription },
        sexualEqualityDescription = extractField { it.sexualEqualityDescription }
    )

fun ProjectManagementEntity.toProjectManagement() = ProjectManagement(
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
    projectCooperationCriteria = projectCooperationCriteria?.ifNotEmpty()?.toCooperationCriteria(),
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
    projectHorizontalPrinciples = projectHorizontalPrinciples?.ifNotEmpty()?.toHorizontalPrinciples(),
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

fun ProjectLongTermPlans.toEntity(projectId: Long) =
    ProjectLongTermPlansEntity(
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

fun List<ProjectLongTermPlansRow>.toProjectLongTermPlans() =
    ProjectLongTermPlans(
        projectOwnership = extractField { it.projectOwnership },
        projectDurability = extractField { it.projectDurability },
        projectTransferability = extractField { it.projectTransferability }
    )

fun ProjectLongTermPlansEntity.toProjectLongTermPlans() = ProjectLongTermPlans(
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

fun ProjectHorizontalPrinciples.toEntity() = ProjectHorizontalPrinciplesEntity(
    sustainableDevelopmentCriteriaEffect = sustainableDevelopmentCriteriaEffect,
    equalOpportunitiesEffect = equalOpportunitiesEffect,
    sexualEqualityEffect = sexualEqualityEffect
)

fun ProjectHorizontalPrinciplesEntity.toHorizontalPrinciples() = ProjectHorizontalPrinciples(
    sustainableDevelopmentCriteriaEffect = sustainableDevelopmentCriteriaEffect,
    equalOpportunitiesEffect = equalOpportunitiesEffect,
    sexualEqualityEffect = sexualEqualityEffect
)

fun ProjectCooperationCriteria.toEntity() = ProjectCooperationCriteriaEntity(
    projectJointDevelopment = projectJointDevelopment,
    projectJointImplementation = projectJointImplementation,
    projectJointStaffing = projectJointStaffing,
    projectJointFinancing = projectJointFinancing
)

fun ProjectCooperationCriteriaEntity.toCooperationCriteria() = ProjectCooperationCriteria(
    projectJointDevelopment = projectJointDevelopment,
    projectJointImplementation = projectJointImplementation,
    projectJointStaffing = projectJointStaffing,
    projectJointFinancing = projectJointFinancing
)

// region Project Overall Objective

fun ProjectOverallObjective.toEntity(projectId: Long) =
    ProjectOverallObjectiveEntity(
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

fun List<ProjectOverallObjectiveRow>.toProjectOverallObjective() =
    ProjectOverallObjective(
        overallObjective = extractField { it.overallObjective }
    )

fun ProjectOverallObjectiveEntity.toProjectOverallObjective() =
    ProjectOverallObjective(
        overallObjective = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.overallObjective) }
    )

// end region Project Overall Objective

// region Project Partnership

fun ProjectPartnership.toEntity(projectId: Long) =
    ProjectPartnershipEntity(
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

fun List<ProjectPartnershipRow>.toProjectPartnership() =
    ProjectPartnership(
        partnership = extractField { it.projectPartnership }
    )

fun ProjectPartnershipEntity.toProjectPartnership() =
    ProjectPartnership(
        partnership = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.projectPartnership) }
    )

// end region Project Partnership
