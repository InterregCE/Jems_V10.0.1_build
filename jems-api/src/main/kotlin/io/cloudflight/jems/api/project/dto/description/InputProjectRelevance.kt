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
    val projectBenefits: Collection<InputProjectRelevanceBenefit>?,    // C2.4

    @field:Size(max = 20, message = "project.description.strategies.too.long")
    val projectStrategies: Collection<InputProjectRelevanceStrategy>?, // C2.5

    @field:Size(max = 20, message = "project.description.synergies.too.long")
    val projectSynergies: Collection<InputProjectRelevanceSynergy>?, // C2.6

    @field:Size(max = 5000, message = "project.description.availableKnowledge.too.long")
    val availableKnowledge: String?  // C2.7
)
