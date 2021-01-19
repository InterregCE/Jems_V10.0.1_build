package io.cloudflight.jems.api.project.dto.description

/**
 * C7.6
 */
data class InputProjectHorizontalPrinciples(
    val sustainableDevelopmentCriteriaEffect: ProjectHorizontalPrinciplesEffect? = null,
    val equalOpportunitiesEffect: ProjectHorizontalPrinciplesEffect? = null,
    val sexualEqualityEffect: ProjectHorizontalPrinciplesEffect? = null
)
