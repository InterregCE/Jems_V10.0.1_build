package io.cloudflight.jems.server.programme.service.stateaid.get_stateaid

import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.stateaid.ProgrammeStateAidPersistence
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetStateAid(
    private val persistence: ProgrammeStateAidPersistence,
) : GetStateAidInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    override fun getStateAidList(): List<ProgrammeStateAid> =
        persistence.getStateAidList()

}
