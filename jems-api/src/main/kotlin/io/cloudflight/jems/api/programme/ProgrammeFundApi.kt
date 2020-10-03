package io.cloudflight.jems.api.programme

import io.cloudflight.jems.api.programme.dto.InputProgrammeFundWrapper
import io.cloudflight.jems.api.programme.dto.OutputProgrammeFund
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Programme Fund")
@RequestMapping("/api/programmefund")
interface ProgrammeFundApi {

    @ApiOperation("Retrieve all programme funds")
    @GetMapping
    fun getProgrammeFundList(pageable: Pageable): List<OutputProgrammeFund>

    @ApiOperation("Specify available Funds for this programme")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeFundList(@Valid @RequestBody fundData: InputProgrammeFundWrapper): List<OutputProgrammeFund>

}
