package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectStatus
import io.cloudflight.ems.entity.ProjectStatus

fun ProjectStatus.toOutputProjectStatus() = OutputProjectStatus(
    id = id,
    status = status,
    user = user.toOutputUser(),
    updated = updated,
    note = note
)
