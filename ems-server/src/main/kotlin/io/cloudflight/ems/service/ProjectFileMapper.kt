package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.entity.ProjectFile

fun ProjectFile.toOutputProjectFile() = OutputProjectFile(
    id,
    name,
    author.toOutputUser(),
    description,
    size,
    updated
)
