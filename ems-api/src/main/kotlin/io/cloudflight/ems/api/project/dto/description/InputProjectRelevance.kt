package io.cloudflight.ems.api.project.dto.description

import javax.validation.constraints.Size

/**
 * C2
 */
data class InputProjectRelevance(
    @field:Size(max = 5000, message = "project.description.territorialChallenge.too.long")
    val territorialChallenge: String?,   // C2.1

    @field:Size(max = 5000, message = "project.description.commonChallenge.too.long")
    val commonChallenge: String?,   // C2.2

    @field:Size(max = 5000, message = "project.description.transnationalCooperation.too.long")
    val transnationalCooperation: String?,  // C2.3

    @field:Size(max = 20, message = "project.description.benefits.too.long")
    val projectBenefits: Collection<InputProjectRelevanceBenefit>?,    // C2.4

    @field:Size(max = 20, message = "project.description.strategies.too.long")
    val projectStrategies: Collection<InputProjectRelevanceStrategy>?, // C2.5

    @field:Size(max = 20, message = "project.description.synergies.too.long")
    val projectSynergies: Collection<InputProjectRelevanceSynergy>?, // C2.6

    @field:Size(max = 5000, message = "project.description.availableKnowledge.too.long")
    val availableKnowledge: String?  // C2.7
)
