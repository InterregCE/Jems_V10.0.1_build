package io.cloudflight.jems.api.programme.fund

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Programme Fund")
interface ProgrammeFundApi {

    companion object {
        private const val ENDPOINT_API_PROGRAMME_FUND = "/api/programmeFund"
    }

    @ApiOperation("Retrieve all programme funds")
    @GetMapping(ENDPOINT_API_PROGRAMME_FUND)
    fun getProgrammeFundList(): List<ProgrammeFundDTO>

    @ApiOperation("Specify available Funds for this programme")
    @PutMapping(ENDPOINT_API_PROGRAMME_FUND, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeFundList(@RequestBody funds: Set<ProgrammeFundDTO>): List<ProgrammeFundDTO>

}
