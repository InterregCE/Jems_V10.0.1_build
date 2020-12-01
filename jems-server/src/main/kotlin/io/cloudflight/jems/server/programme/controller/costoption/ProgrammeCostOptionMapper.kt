package io.cloudflight.jems.server.programme.controller.costoption

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum

fun ProgrammeLumpSum.toDto() = ProgrammeLumpSumDTO(
    id = id,
    name = name,
    description = description,
    cost = cost,
    splittingAllowed = splittingAllowed,
    phase = phase,
    categories = categories
)

fun ProgrammeLumpSumDTO.toModel() = ProgrammeLumpSum(
    id = id,
    name = name,
    description = description,
    cost = cost,
    splittingAllowed = splittingAllowed,
    phase = phase,
    categories = categories
)
