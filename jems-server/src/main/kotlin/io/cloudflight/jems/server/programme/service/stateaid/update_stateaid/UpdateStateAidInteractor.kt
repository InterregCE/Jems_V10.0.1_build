package io.cloudflight.jems.server.programme.service.stateaid.update_stateaid

import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid

interface UpdateStateAidInteractor {

    fun updateStateAids(toDeleteIds: Set<Long>, toPersist: Collection<ProgrammeStateAid>): List<ProgrammeStateAid>
}
