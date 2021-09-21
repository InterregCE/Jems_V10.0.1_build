package io.cloudflight.jems.server.programme.controller.stateaid

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidDTO
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidUpdateDTO
import io.cloudflight.jems.api.programme.stateaid.ProgrammeStateAidApi
import io.cloudflight.jems.server.programme.service.stateaid.get_stateaid.GetStateAidInteractor
import io.cloudflight.jems.server.programme.service.stateaid.update_stateaid.UpdateStateAidInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeStateAidController(
    private val getStateAid: GetStateAidInteractor,
    private val updateStateAid: UpdateStateAidInteractor
) : ProgrammeStateAidApi {

    override fun getProgrammeStateAidList(): List<ProgrammeStateAidDTO> =
        getStateAid.getStateAidList().toDto()

    override fun updateProgrammeStateAids(stateAidData: ProgrammeStateAidUpdateDTO): List<ProgrammeStateAidDTO> =
        updateStateAid.updateStateAids(
            toDeleteIds = stateAidData.toDeleteIds,
            toPersist = stateAidData.toPersist.toModel(),
        ).toDto()


}
