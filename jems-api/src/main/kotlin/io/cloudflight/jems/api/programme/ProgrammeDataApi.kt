package io.cloudflight.jems.api.programme

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.programme.dto.ProgrammeDataUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.ProgrammeDataDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Api("Programme Data")
interface ProgrammeDataApi {

    companion object {
        private const val ENDPOINT_API_PROGRAMME_DATA = "/api/programmedata"
    }

    @ApiOperation("Retrieve programme setup")
    @GetMapping(ENDPOINT_API_PROGRAMME_DATA)
    fun get(): ProgrammeDataDTO

    @ApiOperation("Specify base data for this programme")
    @PutMapping(ENDPOINT_API_PROGRAMME_DATA, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@RequestBody updateRequestDTO: ProgrammeDataUpdateRequestDTO): ProgrammeDataDTO

    @ApiOperation("Specify available NUTS regions for this programme")
    @PutMapping("$ENDPOINT_API_PROGRAMME_DATA/nuts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateNuts(@RequestBody regions: Collection<String>): ProgrammeDataDTO

    @ApiOperation("Retrieve NUTS available for this programme setup")
    @GetMapping("$ENDPOINT_API_PROGRAMME_DATA/nuts")
    fun getNuts(): List<OutputNuts>

    @ApiOperation("Check if programme setup is still open for changes or not yet")
    @GetMapping("$ENDPOINT_API_PROGRAMME_DATA/isLocked")
    fun isProgrammeSetupLocked(): Boolean

    @ApiOperation("Check if programme has projects in status")
    @GetMapping("$ENDPOINT_API_PROGRAMME_DATA/hasProjectsInStatus/{projectStatus}")
    fun hasProjectsInStatus(@PathVariable projectStatus: ApplicationStatusDTO): Boolean

}
