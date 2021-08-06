package io.cloudflight.jems.server.programme.service.stateaid

import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid

interface ProgrammeStateAidPersistence {
    fun getStateAidList(): List<ProgrammeStateAid>

    fun updateStateAids(
        toDeleteIds: Set<Long>,
        toPersist: Collection<ProgrammeStateAid>
    ): List<ProgrammeStateAid>
}
