package io.cloudflight.jems.server.programme.controller.typologyerrors

import io.cloudflight.jems.api.programme.dto.typologyerrors.TypologyErrorsDTO
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors

fun Iterable<TypologyErrors>.toDto() = map { it.toDto() }

fun TypologyErrors.toDto() = TypologyErrorsDTO(
    id = id,
    description = description,
)

fun Iterable<TypologyErrorsDTO>.toModel() = map {
    TypologyErrors(
        id = it.id ?: 0,
        description = it.description,
    )
}
