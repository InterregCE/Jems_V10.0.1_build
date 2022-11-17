package io.cloudflight.jems.server.programme.service.typologyerrors

import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors

interface UpdateTypologyErrorsInteractor {

    fun updateTypologyErrors(
        toDeleteIds: List<Long>,
        toPersist: List<TypologyErrors>
    ): List<TypologyErrors>
}
