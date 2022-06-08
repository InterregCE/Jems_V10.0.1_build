package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectRelevanceSpfRecipient(
    val recipientGroup: ProjectTargetGroup,
    val specification: Set<InputTranslation> = emptySet()
)
