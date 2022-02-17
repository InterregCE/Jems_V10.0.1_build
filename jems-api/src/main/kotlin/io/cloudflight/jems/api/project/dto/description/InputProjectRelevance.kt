package io.cloudflight.jems.api.project.dto.description

import io.cloudflight.jems.api.project.dto.InputTranslation

/**
 * C2
 */
data class InputProjectRelevance(

    val territorialChallenge: Set<InputTranslation> = emptySet(),   // C2.1

    val commonChallenge: Set<InputTranslation> = emptySet(),   // C2.2

    val transnationalCooperation: Set<InputTranslation> = emptySet(),  // C2.3

    val projectBenefits: List<InputProjectRelevanceBenefit>?,    // C2.4

    val projectSpfRecipients: List<ProjectRelevanceSpfRecipientDTO>?,    // C2.4a

    val projectStrategies: List<InputProjectRelevanceStrategy>?, // C2.5

    val projectSynergies: List<InputProjectRelevanceSynergy>?, // C2.6

    val availableKnowledge: Set<InputTranslation> = emptySet()  // C2.7

)
