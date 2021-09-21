package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectLongTermPlans(
    val projectOwnership: Set<InputTranslation> = emptySet(),
    val projectDurability: Set<InputTranslation> = emptySet(),
    val projectTransferability: Set<InputTranslation> = emptySet()
)
