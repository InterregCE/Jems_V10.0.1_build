package io.cloudflight.ems.api.nuts

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Programme Nuts Setup")
@RequestMapping("/api/programmenuts")
interface ProgrammeNutsApi {

    @ApiOperation("Retrieve all possible NUTS regions for this programme")
    @GetMapping
    fun getProgrammeNuts(): Any

    @ApiOperation("Specify available NUTS regions for this programme")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeNuts(@Valid @RequestBody regions: Collection<String>): Any

}
