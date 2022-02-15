package io.cloudflight.jems.api.programme

import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Programme Strategy")
interface ProgrammeStrategyApi {

    companion object {
        private const val ENDPOINT_API_CALL_FIELD_CONFIG = "/api/programmestrategy"
    }

    @ApiOperation("Returns all strategies")
    @GetMapping(ENDPOINT_API_CALL_FIELD_CONFIG)
    fun getProgrammeStrategies(): List<OutputProgrammeStrategy>

    @ApiOperation("Update selected strategies")
    @PutMapping(ENDPOINT_API_CALL_FIELD_CONFIG, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeStrategies(@RequestBody strategies: List<InputProgrammeStrategy>): List<OutputProgrammeStrategy>
}
