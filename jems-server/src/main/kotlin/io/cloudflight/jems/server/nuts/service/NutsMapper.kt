package io.cloudflight.jems.server.nuts.service

import io.cloudflight.jems.api.nuts.dto.OutputNutsMetadata
import io.cloudflight.jems.server.nuts.entity.NutsBaseEntity
import io.cloudflight.jems.server.nuts.entity.NutsMetadata

fun NutsMetadata.toOutputNutsMetadata() = OutputNutsMetadata(
    title = nutsTitle,
    date = nutsDate
)

fun NutsBaseEntity.toOutput() = NutsIdentifier(
    id = id,
    title = title
)
