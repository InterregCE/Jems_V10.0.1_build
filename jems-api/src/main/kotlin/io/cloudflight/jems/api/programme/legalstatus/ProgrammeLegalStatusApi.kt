package io.cloudflight.jems.api.programme.legalstatus

import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusUpdateDTO
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Programme Legal Status")
interface ProgrammeLegalStatusApi {

    companion object {
        private const val ENDPOINT_API_PROGRAMME_LEGAL_STATUS = "/api/programmeLegalStatus"
    }

    @ApiOperation("Retrieve all programme legal statuses")
    @GetMapping(ENDPOINT_API_PROGRAMME_LEGAL_STATUS)
    fun getProgrammeLegalStatusList(): List<ProgrammeLegalStatusDTO>

    @ApiOperation("Specify available legal statuses for this programme")
    @PutMapping(ENDPOINT_API_PROGRAMME_LEGAL_STATUS, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeLegalStatuses(@RequestBody statusData: ProgrammeLegalStatusUpdateDTO): List<ProgrammeLegalStatusDTO>

}
