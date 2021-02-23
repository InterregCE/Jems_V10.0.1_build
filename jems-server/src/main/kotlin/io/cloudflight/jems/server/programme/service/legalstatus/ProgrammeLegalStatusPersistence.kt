package io.cloudflight.jems.server.programme.service.legalstatus

import io.cloudflight.jems.server.programme.service.ProgrammePersistence
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus

interface ProgrammeLegalStatusPersistence : ProgrammePersistence {

    fun getMax20Statuses(): List<ProgrammeLegalStatus>

    fun updateLegalStatuses(
        toDeleteIds: Set<Long>,
        toPersist: Collection<ProgrammeLegalStatus>
    ): List<ProgrammeLegalStatus>

    fun getCount(): Long

}
