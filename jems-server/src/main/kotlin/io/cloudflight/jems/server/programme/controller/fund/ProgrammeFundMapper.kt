package io.cloudflight.jems.server.programme.controller.fund

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType

fun ProgrammeFund.toDto() =
    ProgrammeFundDTO(
        id = id,
        selected = selected,
        type = ProgrammeFundTypeDTO.valueOf(type.name),
        abbreviation = abbreviation,
        description = description,
    )

fun Iterable<ProgrammeFund>.toDto() = map { it.toDto() }

fun Iterable<ProgrammeFundDTO>.toModel() = map {
    ProgrammeFund(
        id = it.id ?: 0,
        selected = it.selected,
        type = ProgrammeFundType.valueOf(it.type.name),
        abbreviation = it.abbreviation,
        description = it.description
    )
}
