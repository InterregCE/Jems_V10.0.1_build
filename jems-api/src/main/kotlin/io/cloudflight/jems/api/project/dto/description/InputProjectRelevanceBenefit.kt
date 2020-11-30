package io.cloudflight.jems.api.project.dto.description

import io.cloudflight.jems.api.project.dto.InputTranslation

data class InputProjectRelevanceBenefit(
    val group: ProjectTargetGroup,
    val specification: Set<InputTranslation> = emptySet()
)
