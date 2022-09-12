package io.cloudflight.jems.server.programme.service.typologyerrors

import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetTypologyErrorsService(
    private val persistence: ProgrammeTypologyErrorsPersistence
) : GetTypologyErrorsInteractor {

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    override fun getTypologyErrors(): List<TypologyErrors> {
        return persistence.getAllTypologyErrors()
    }

}
