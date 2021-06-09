package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup

data class ProjectRelevanceBenefit(
    val group: ProjectTargetGroup,
    val specification: Set<InputTranslation> = emptySet()
)
