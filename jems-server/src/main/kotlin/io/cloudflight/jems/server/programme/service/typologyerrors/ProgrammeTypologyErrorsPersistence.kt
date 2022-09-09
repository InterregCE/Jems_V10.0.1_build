package io.cloudflight.jems.server.programme.service.typologyerrors

import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors

interface ProgrammeTypologyErrorsPersistence {

    fun getAllTypologyErrors(): List<TypologyErrors>

    fun updateLegalStatuses(
        toDeleteIds: List<Long>,
        toPersist: List<TypologyErrors>
    ): List<TypologyErrors>
}
