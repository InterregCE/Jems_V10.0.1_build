package io.cloudflight.jems.api.project.dto.description

import io.cloudflight.jems.api.project.dto.InputTranslation
import javax.validation.constraints.Size

/**
 * C2
 */
data class InputProjectRelevance(

    val territorialChallenge: Set<InputTranslation> = emptySet(),   // C2.1

    val commonChallenge: Set<InputTranslation> = emptySet(),   // C2.2

    val transnationalCooperation: Set<InputTranslation> = emptySet(),  // C2.3

    @field:Size(max = 20, message = "project.description.benefits.too.long")
    val projectBenefits: List<InputProjectRelevanceBenefit>?,    // C2.4

    @field:Size(max = 20, message = "project.description.strategies.too.long")
    val projectStrategies: List<InputProjectRelevanceStrategy>?, // C2.5

    @field:Size(max = 20, message = "project.description.synergies.too.long")
    val projectSynergies: List<InputProjectRelevanceSynergy>?, // C2.6

    val availableKnowledge: Set<InputTranslation> = emptySet()  // C2.7

)
