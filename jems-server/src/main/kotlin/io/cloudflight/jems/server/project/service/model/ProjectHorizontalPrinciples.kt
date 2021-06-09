package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect

data class ProjectHorizontalPrinciples(
    val sustainableDevelopmentCriteriaEffect: ProjectHorizontalPrinciplesEffect? = null,
    val equalOpportunitiesEffect: ProjectHorizontalPrinciplesEffect? = null,
    val sexualEqualityEffect: ProjectHorizontalPrinciplesEffect? = null
)
