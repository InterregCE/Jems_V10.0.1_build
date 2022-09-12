package io.cloudflight.jems.api.programme.typologyerrors

import io.cloudflight.jems.api.programme.dto.typologyerrors.TypologyErrorsDTO
import io.cloudflight.jems.api.programme.dto.typologyerrors.TypologyErrorsUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Programme Typology of Errors")
interface ProgrammeTypologyErrorsApi {

    companion object {
        private const val ENDPOINT_API_TYPOLOGY_ERRORS = "/api/programmeTypologyErrors"
    }

    @ApiOperation("Retrieve all programme typology errors")
    @GetMapping(ProgrammeTypologyErrorsApi.ENDPOINT_API_TYPOLOGY_ERRORS)
    fun getTypologyErrors(): List<TypologyErrorsDTO>

    @ApiOperation("Specify typology errors for this programme")
    @PutMapping(ProgrammeTypologyErrorsApi.ENDPOINT_API_TYPOLOGY_ERRORS, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateTypologyErrors(@RequestBody statusData: TypologyErrorsUpdateDTO): List<TypologyErrorsDTO>

}
