package io.cloudflight.jems.api.programme

import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Programme Strategy")
@RequestMapping("/api/programmestrategy")
interface ProgrammeStrategyApi {

    @ApiOperation("Returns all strategies")
    @GetMapping
    fun getProgrammeStrategies(): List<OutputProgrammeStrategy>

    @ApiOperation("Update selected strategies")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeStrategies(@Valid @RequestBody strategies: List<InputProgrammeStrategy>): List<OutputProgrammeStrategy>
}
