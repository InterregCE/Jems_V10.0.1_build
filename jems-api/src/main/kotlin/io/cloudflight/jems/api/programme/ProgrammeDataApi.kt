package io.cloudflight.jems.api.programme

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.programme.dto.InputProgrammeData
import io.cloudflight.jems.api.programme.dto.OutputProgrammeData
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Programme Data")
@RequestMapping("/api/programmedata")
interface ProgrammeDataApi {

    @ApiOperation("Retrieve programme setup")
    @GetMapping
    fun get(): OutputProgrammeData

    @ApiOperation("Specify base data for this programme")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@Valid @RequestBody programmeData: InputProgrammeData): OutputProgrammeData

    @ApiOperation("Specify available NUTS regions for this programme")
    @PutMapping("/nuts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateNuts(@Valid @RequestBody regions: Collection<String>): OutputProgrammeData

    @ApiOperation("Retrieve NUTS available for this programme setup")
    @GetMapping("/nuts")
    fun getNuts(): List<OutputNuts>

    @ApiOperation("Check if programme setup is still open for changes or not yet")
    @GetMapping("/isLocked")
    fun isProgrammeSetupLocked(): Boolean

}
