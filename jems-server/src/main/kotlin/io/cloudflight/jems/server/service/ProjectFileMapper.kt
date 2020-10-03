package io.cloudflight.jems.server.service

import io.cloudflight.jems.api.dto.OutputProjectFile
import io.cloudflight.jems.server.entity.ProjectFile
import io.cloudflight.jems.server.user.service.toOutputUser

fun ProjectFile.toOutputProjectFile() = OutputProjectFile(
    id,
    name,
    author.toOutputUser(),
    type,
    description,
    size,
    updated
)
