package io.cloudflight.jems.server.programme.service.legalstatus.get_legal_statuses

import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.legalstatus.ProgrammeLegalStatusPersistence
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetLegalStatus(
    private val persistence: ProgrammeLegalStatusPersistence,
) : GetLegalStatusInteractor {

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    override fun getLegalStatuses(): List<ProgrammeLegalStatus> =
        persistence.getMax20Statuses()

}
