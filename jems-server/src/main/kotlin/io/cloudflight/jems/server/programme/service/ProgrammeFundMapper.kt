package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeFund
import io.cloudflight.jems.api.programme.dto.ProgrammeFundOutputDTO
import io.cloudflight.jems.server.programme.entity.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.service.model.ProgrammeFund

fun InputProgrammeFund.toModel() = ProgrammeFund(
    id = id ?: 0,
    abbreviation = if (id == null) abbreviation else null,
    description = if (id == null) description else null,
    selected = selected
)

fun ProgrammeFund.toDto(): ProgrammeFundOutputDTO {
    return if (id <= 9) { // only first 9 funds are translated
        ProgrammeFundOutputDTO(id = id, selected = selected)
    } else {
        ProgrammeFundOutputDTO(id = id, abbreviation = abbreviation, description = description, selected = selected)
    }
}

// TODO remove all of those when switched to models:
fun InputProgrammeFund.toEntity() = ProgrammeFundEntity(
    id = id ?: 0,
    abbreviation = if (id == null) abbreviation else null,
    description = if (id == null) description else null,
    selected = selected
)

fun ProgrammeFundOutputDTO.toEntity() = ProgrammeFundEntity(
    id = id,
    abbreviation = abbreviation,
    description = description,
    selected = selected
)

fun ProgrammeFundEntity.toOutputProgrammeFund(): ProgrammeFundOutputDTO {
    return if (id <= 9) { // only first 9 funds are translated
        ProgrammeFundOutputDTO(id = id, selected = selected)
    } else {
        ProgrammeFundOutputDTO(id = id, abbreviation = abbreviation, description = description, selected = selected)
    }
}
