package io.cloudflight.jems.server.programme.service.legalstatus

import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType

interface ProgrammeLegalStatusPersistence {

    fun getMax20Statuses(): List<ProgrammeLegalStatus>

    fun updateLegalStatuses(
        toDeleteIds: Set<Long>,
        toPersist: Collection<ProgrammeLegalStatus>
    ): List<ProgrammeLegalStatus>

    fun getByType(types: List<ProgrammeLegalStatusType>): List<ProgrammeLegalStatus>
    fun getCount(): Long

}
