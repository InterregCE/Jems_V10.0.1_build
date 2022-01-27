package io.cloudflight.jems.api.programme

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.programme.dto.ProgrammeDataUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.ProgrammeDataDTO
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
    fun get(): ProgrammeDataDTO

    @ApiOperation("Specify base data for this programme")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@Valid @RequestBody updateRequestDTO: ProgrammeDataUpdateRequestDTO): ProgrammeDataDTO

    @ApiOperation("Specify available NUTS regions for this programme")
    @PutMapping("/nuts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateNuts(@Valid @RequestBody regions: Collection<String>): ProgrammeDataDTO

    @ApiOperation("Retrieve NUTS available for this programme setup")
    @GetMapping("/nuts")
    fun getNuts(): List<OutputNuts>

    @ApiOperation("Check if programme setup is still open for changes or not yet")
    @GetMapping("/isLocked")
    fun isProgrammeSetupLocked(): Boolean

}
