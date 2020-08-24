package io.cloudflight.ems.nuts.service

import io.cloudflight.ems.api.nuts.dto.OutputNutsMetadata
import io.cloudflight.ems.nuts.entity.NutsBaseEntity
import io.cloudflight.ems.nuts.entity.NutsMetadata

fun NutsMetadata.toOutputNutsMetadata() = OutputNutsMetadata(
    title = nutsTitle,
    date = nutsDate
)

fun NutsBaseEntity.toOutput() = NutsIdentifier(
    id = id,
    title = title
)
