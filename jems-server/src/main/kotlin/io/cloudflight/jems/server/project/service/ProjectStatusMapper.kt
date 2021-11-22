package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.user.service.toOutputUser

fun ProjectStatusHistoryEntity.toOutputProjectStatus() = ProjectStatusDTO(
        id = id,
        status = ApplicationStatusDTO.valueOf(status.name),
        user = user.toOutputUser(),
        updated = updated,
        decisionDate = decisionDate,
        entryIntoForceDate = entryIntoForceDate,
        note = note
)
