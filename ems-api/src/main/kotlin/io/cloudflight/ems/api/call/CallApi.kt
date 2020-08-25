package io.cloudflight.ems.api.call

import io.cloudflight.ems.api.call.dto.InputCallCreate
import io.cloudflight.ems.api.call.dto.InputCallUpdate
import io.cloudflight.ems.api.call.dto.OutputCall
import io.cloudflight.ems.api.call.dto.OutputCallList
import io.cloudflight.ems.api.call.dto.OutputCallProgrammePriority
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.validation.Valid

@Api("Call")
@RequestMapping("/api/call")
interface CallApi {

    @ApiOperation("Returns all calls")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping
    fun getCalls(pageable: Pageable): Page<OutputCallList>

    @ApiOperation("Returns a call by call id")
    @GetMapping("/{id}")
    fun getCallById(@PathVariable id: Long): OutputCall

    @ApiOperation("Create a new call")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createCall(@Valid @RequestBody call: InputCallCreate): OutputCall

    @ApiOperation("Update an existing call")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCall(@Valid @RequestBody call: InputCallUpdate): OutputCall

    @ApiOperation("Publish existing call (which is now DRAFT)")
    @PutMapping("/{id}/publish")
    fun publishCall(@PathVariable id: Long): OutputCall

    @GetMapping("/objectives/{callId}")
    fun getCallObjectives(@PathVariable callId: Long): List<OutputCallProgrammePriority>

}
