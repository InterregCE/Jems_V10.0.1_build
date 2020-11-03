package io.cloudflight.jems.server.project.service.file

import io.cloudflight.jems.api.project.dto.file.OutputProjectFile
import io.cloudflight.jems.server.project.entity.file.ProjectFile
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
