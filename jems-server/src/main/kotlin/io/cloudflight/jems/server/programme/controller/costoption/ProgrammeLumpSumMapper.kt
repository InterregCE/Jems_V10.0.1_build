package io.cloudflight.jems.server.programme.controller.costoption

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumListDTO
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum

fun ProgrammeLumpSum.toDto() = ProgrammeLumpSumDTO(
    id = id,
    name = name,
    description = description,
    cost = cost,
    splittingAllowed = splittingAllowed,
    fastTrack = fastTrack,
    phase = phase,
    categories = categories
)

fun Iterable<ProgrammeLumpSum>.toDto() = map {
    ProgrammeLumpSumListDTO(
        id = it.id,
        name = it.name,
        cost = it.cost,
        splittingAllowed = it.splittingAllowed,
    )
}

fun ProgrammeLumpSumDTO.toModel() = ProgrammeLumpSum(
    id = id ?: 0,
    name = name,
    description = description,
    cost = cost,
    splittingAllowed = splittingAllowed,
    fastTrack = fastTrack,
    phase = phase,
    categories = categories
)
