package io.cloudflight.jems.server.programme.service.legalstatus.update_legal_statuses

import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus

interface UpdateLegalStatusInteractor {

    fun updateLegalStatuses(
        toDeleteIds: Set<Long>,
        toPersist: Collection<ProgrammeLegalStatus>
    ): List<ProgrammeLegalStatus>

}
