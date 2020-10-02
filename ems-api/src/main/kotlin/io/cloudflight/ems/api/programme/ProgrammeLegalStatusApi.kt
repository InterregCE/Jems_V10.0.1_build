package io.cloudflight.ems.api.programme

import io.cloudflight.ems.api.programme.dto.InputProgrammeLegalStatusWrapper
import io.cloudflight.ems.api.programme.dto.OutputProgrammeLegalStatus
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Programme Legal Status")
@RequestMapping("/api/programmelegalstatus")
interface ProgrammeLegalStatusApi {
    @ApiOperation("Retrieve all programme legal statuses")
    @GetMapping
    fun getProgrammeLegalStatusList(pageable: Pageable): List<OutputProgrammeLegalStatus>

    @ApiOperation("Specify available legal statuses for this programme")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addProgrammeLegalStatuses(@Valid @RequestBody statusData: InputProgrammeLegalStatusWrapper): List<OutputProgrammeLegalStatus>

    @ApiOperation("Delete legal status")
    @DeleteMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun delete(@PathVariable id: Long)
}
