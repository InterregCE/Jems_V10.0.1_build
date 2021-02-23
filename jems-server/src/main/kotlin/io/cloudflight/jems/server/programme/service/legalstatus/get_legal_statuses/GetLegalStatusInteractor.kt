package io.cloudflight.jems.server.programme.service.legalstatus.get_legal_statuses

import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetLegalStatusInteractor {

    fun getLegalStatuses(): List<ProgrammeLegalStatus>

}
