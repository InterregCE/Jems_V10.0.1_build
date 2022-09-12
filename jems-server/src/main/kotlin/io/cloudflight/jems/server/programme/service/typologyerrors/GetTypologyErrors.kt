package io.cloudflight.jems.server.programme.service.typologyerrors

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.typologyerrors.exception.GetTypologyErrorsFailedException
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetTypologyErrors(
    private val persistence: ProgrammeTypologyErrorsPersistence
) : GetTypologyErrorsInteractor {

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetTypologyErrorsFailedException::class)
    override fun getTypologyErrors(): List<TypologyErrors> {
        return persistence.getAllTypologyErrors()
    }

}
