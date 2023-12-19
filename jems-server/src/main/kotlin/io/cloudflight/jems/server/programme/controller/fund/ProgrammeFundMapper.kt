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

fun Iterable<ProgrammeFundDTO>.toModelList() = map { it.toModel() }

fun ProgrammeFundDTO.toModel() = ProgrammeFund(
    id = id ?: 0,
    selected = selected,
    type = ProgrammeFundType.valueOf(type.name),
    abbreviation = abbreviation,
    description = description
)

fun List<ProgrammeFundType>.toDto() = map { it.toDto() }

fun ProgrammeFundType.toDto() =
    ProgrammeFundTypeDTO.valueOf(this.toString())

fun ProgrammeFundTypeDTO.toModel() =
    ProgrammeFundType.valueOf(this.toString())
