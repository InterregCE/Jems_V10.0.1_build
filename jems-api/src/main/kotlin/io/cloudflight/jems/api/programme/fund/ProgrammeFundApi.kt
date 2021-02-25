package io.cloudflight.jems.api.programme.fund

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Api("Programme Fund")
@RequestMapping("/api/programmeFund")
interface ProgrammeFundApi {

    @ApiOperation("Retrieve all programme funds")
    @GetMapping
    fun getProgrammeFundList(): List<ProgrammeFundDTO>

    @ApiOperation("Specify available Funds for this programme")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeFundList(@RequestBody funds: Set<ProgrammeFundDTO>): List<ProgrammeFundDTO>

}
