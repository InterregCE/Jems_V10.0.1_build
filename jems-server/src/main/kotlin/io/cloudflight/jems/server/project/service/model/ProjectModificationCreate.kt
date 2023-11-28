package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo

data class ProjectModificationCreate(
    val actionInfo: ApplicationActionInfo,
    val correctionIds: Set<Long> = emptySet(),
)
