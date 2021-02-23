package io.cloudflight.jems.api.programme

import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorSummaryDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorDetailDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Programme Indicator")
@RequestMapping("/api/programmeindicator/output")
interface OutputIndicatorApi {

    @ApiOperation("Creates new OUTPUT indicator")
    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createOutputIndicator(@Valid @RequestBody outputIndicatorCreateRequestDTO: OutputIndicatorCreateRequestDTO): OutputIndicatorDetailDTO

    @ApiOperation("Updates existing OUTPUT indicator")
    @PutMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateOutputIndicator(@Valid @RequestBody outputIndicatorUpdateRequestDTO: OutputIndicatorUpdateRequestDTO): OutputIndicatorDetailDTO

    @ApiOperation("Returns all OUTPUT indicators")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("")
    fun getOutputIndicatorDetails(pageable: Pageable): Page<OutputIndicatorDetailDTO>

    @ApiOperation("Returns OUTPUT indicator by id")
    @GetMapping("/{id}")
    fun getOutputIndicatorDetail(@PathVariable id: Long): OutputIndicatorDetailDTO

    @GetMapping("/summary")
    fun getOutputIndicatorSummaries(): Set<OutputIndicatorSummaryDTO>

    @GetMapping("/summary/{programmeObjectivePolicy}")
    fun getOutputIndicatorSummariesForSpecificObjective(@PathVariable programmeObjectivePolicy: ProgrammeObjectivePolicy): List<OutputIndicatorSummaryDTO>

}
