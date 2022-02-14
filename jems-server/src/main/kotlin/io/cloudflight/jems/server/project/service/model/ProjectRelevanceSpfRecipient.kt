package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO

data class ProjectRelevanceSpfRecipient(
    val recipientGroup: ProjectTargetGroupDTO,
    val specification: Set<InputTranslation> = emptySet()
)
