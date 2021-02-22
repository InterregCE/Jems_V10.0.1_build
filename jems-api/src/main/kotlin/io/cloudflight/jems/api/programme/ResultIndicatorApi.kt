package io.cloudflight.jems.api.programme

import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorSummaryDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorDetailDTO
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
@RequestMapping("/api/programmeindicator/result")
interface ResultIndicatorApi {

    @ApiOperation("Creates new RESULT indicator")
    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createResultIndicator(@Valid @RequestBody resultIndicatorCreateRequestDTO: ResultIndicatorCreateRequestDTO): ResultIndicatorDetailDTO

    @ApiOperation("Updates existing RESULT indicator")
    @PutMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateResultIndicator(@Valid @RequestBody resultIndicatorUpdateRequestDTO: ResultIndicatorUpdateRequestDTO): ResultIndicatorDetailDTO


    @ApiOperation("Returns all RESULT indicators")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("")
    fun getResultIndicatorDetails(pageable: Pageable): Page<ResultIndicatorDetailDTO>

    @ApiOperation("Returns RESULT indicator by id")
    @GetMapping("/{id}")
    fun getResultIndicatorDetail(@PathVariable id: Long): ResultIndicatorDetailDTO

    @GetMapping("/summary")
    fun getResultIndicatorSummaries(): Set<ResultIndicatorSummaryDTO>

    @GetMapping("/summary/{programmeObjectivePolicy}")
    fun getResultIndicatorSummariesForSpecificObjective(@PathVariable programmeObjectivePolicy: ProgrammeObjectivePolicy): List<ResultIndicatorSummaryDTO>

}
