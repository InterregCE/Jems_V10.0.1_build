package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.entity.ProjectFile
import io.cloudflight.ems.user.service.toOutputUser

fun ProjectFile.toOutputProjectFile() = OutputProjectFile(
    id,
    name,
    author.toOutputUser(),
    type,
    description,
    size,
    updated
)
