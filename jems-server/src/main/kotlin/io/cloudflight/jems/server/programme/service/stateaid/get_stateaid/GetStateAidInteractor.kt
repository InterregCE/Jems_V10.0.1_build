package io.cloudflight.jems.server.programme.service.stateaid.get_stateaid

import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid

interface GetStateAidInteractor {

    fun getStateAidList(): List<ProgrammeStateAid>
}
