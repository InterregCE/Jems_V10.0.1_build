package io.cloudflight.ems.api.project.dto.description

/**
 * C7.6
 */
data class InputProjectHorizontalPrinciples (
    val sustainableDevelopmentCriteriaEffect: ProjectHorizontalPrinciplesEffect? = null,
    val sustainableDevelopmentDescription: String? = null,
    val equalOpportunitiesEffect: ProjectHorizontalPrinciplesEffect? = null,
    val equalOpportunitiesDescription: String? = null,
    val sexualEqualityEffect: ProjectHorizontalPrinciplesEffect? = null,
    val sexualEqualityDescription: String? = null
)
