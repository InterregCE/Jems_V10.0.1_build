package io.cloudflight.jems.api.project.dto.description

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectRelevanceSpfRecipientDTO(
    val recipientGroup: ProjectTargetGroupDTO,
    val specification: Set<InputTranslation> = emptySet()
)
