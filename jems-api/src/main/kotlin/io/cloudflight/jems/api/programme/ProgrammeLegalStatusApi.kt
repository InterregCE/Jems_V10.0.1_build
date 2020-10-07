package io.cloudflight.jems.api.programme

import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatusWrapper
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLegalStatus
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Programme Legal Status")
@RequestMapping("/api/programmelegalstatus")
interface ProgrammeLegalStatusApi {
    @ApiOperation("Retrieve all programme legal statuses")
    @GetMapping
    fun getProgrammeLegalStatusList(pageable: Pageable): List<OutputProgrammeLegalStatus>

    @ApiOperation("Specify available legal statuses for this programme")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeLegalStatuses(@Valid @RequestBody statusData: InputProgrammeLegalStatusWrapper): List<OutputProgrammeLegalStatus>
}
