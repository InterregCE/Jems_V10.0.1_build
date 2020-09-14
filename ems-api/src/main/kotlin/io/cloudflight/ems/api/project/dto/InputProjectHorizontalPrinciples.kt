package io.cloudflight.ems.api.project.dto

data class InputProjectHorizontalPrinciples (
    val sustainableDevelopmentCriteriaEffect: ProjectHorizontalPrinciplesEffect?,
    val sustainableDevelopmentDescription: String?,
    val equalOpportunitiesEffect: ProjectHorizontalPrinciplesEffect?,
    val equalOpportunitiesDescription: String?,
    val sexualEqualityEffect: ProjectHorizontalPrinciplesEffect?,
    val sexualEqualityDescription: String?
)