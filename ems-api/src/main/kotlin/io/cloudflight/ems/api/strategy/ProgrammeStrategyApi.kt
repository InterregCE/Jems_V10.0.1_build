package io.cloudflight.ems.api.strategy

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Programme Strategy")
@RequestMapping("/api/strategy")
interface ProgrammeStrategyApi {

    @ApiOperation("Returns all strategies")
    @GetMapping()
    fun getProgrammeStrategies(): List<OutputProgrammeStrategy>

    @ApiOperation("Update selected strategies")
    @PutMapping("/changeStrategies", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeStrategies(@Valid @RequestBody strategies: List<InputProgrammeStrategy>): List<OutputProgrammeStrategy>
}
