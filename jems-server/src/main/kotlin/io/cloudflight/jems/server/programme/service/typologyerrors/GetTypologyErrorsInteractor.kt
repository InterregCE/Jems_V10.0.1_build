package io.cloudflight.jems.server.programme.service.typologyerrors

import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors

interface GetTypologyErrorsInteractor {
    fun getTypologyErrors(): List<TypologyErrors>
}
