package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.server.programme.entity.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.service.model.ProgrammeFund

fun ProgrammeFund.toEntity() = ProgrammeFundEntity(
    id = id,
    abbreviation = abbreviation,
    description = description,
    selected = selected
)

fun ProgrammeFundEntity.toModel(): ProgrammeFund {
    return if (id <= 9) { // only first 9 funds are translated
        ProgrammeFund(id = id, selected = selected)
    } else {
        ProgrammeFund(id = id, abbreviation = abbreviation, description = description, selected = selected)
    }
}
