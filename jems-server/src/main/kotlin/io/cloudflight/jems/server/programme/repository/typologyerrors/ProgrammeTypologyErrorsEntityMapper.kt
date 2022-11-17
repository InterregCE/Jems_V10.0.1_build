package io.cloudflight.jems.server.programme.repository.typologyerrors

import io.cloudflight.jems.server.programme.entity.typologyerrors.ProgrammeTypologyErrorsEntity
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors

fun Iterable<ProgrammeTypologyErrorsEntity>.toModel() = map { it.toModel() }

fun ProgrammeTypologyErrorsEntity.toModel() = TypologyErrors(
    id = id,
    description = description
)

fun Collection<TypologyErrors>.toEntity() = map { model ->
    ProgrammeTypologyErrorsEntity(
        id = model.id,
        description = model.description
    )
}
