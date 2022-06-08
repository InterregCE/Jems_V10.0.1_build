package io.cloudflight.jems.api.programme.stateaid

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidDTO
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Programme State Aid")
interface ProgrammeStateAidApi {

    companion object {
        private const val ENDPOINT_API_PROGRAMME_STATE_AID = "/api/programmeStateAid"
    }

    @ApiOperation("Retrieve all programme state aids")
    @GetMapping(ENDPOINT_API_PROGRAMME_STATE_AID)
    fun getProgrammeStateAidList(): List<ProgrammeStateAidDTO>

    @ApiOperation("Specify available state aids for this programme")
    @PostMapping(ENDPOINT_API_PROGRAMME_STATE_AID, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeStateAids(@RequestBody stateAidData: ProgrammeStateAidUpdateDTO): List<ProgrammeStateAidDTO>

}
