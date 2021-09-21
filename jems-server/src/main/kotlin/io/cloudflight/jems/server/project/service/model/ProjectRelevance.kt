package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectRelevance(
    val territorialChallenge: Set<InputTranslation> = emptySet(),
    val commonChallenge: Set<InputTranslation> = emptySet(),
    val transnationalCooperation: Set<InputTranslation> = emptySet(),
    val projectBenefits: List<ProjectRelevanceBenefit>?,
    val projectStrategies: List<ProjectRelevanceStrategy>?,
    val projectSynergies: List<ProjectRelevanceSynergy>?,
    val availableKnowledge: Set<InputTranslation> = emptySet()
)
