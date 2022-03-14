package io.cloudflight.jems.api.call

import io.cloudflight.jems.api.call.dto.AllowedRealCostsDTO
import io.cloudflight.jems.api.call.dto.CallDTO
import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallUpdateRequestDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.common.dto.IdNamePairDTO
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
import org.springframework.web.bind.annotation.RequestParam

@Api("Call")
interface CallApi {

    companion object {
        private const val ENDPOINT_API_CALL = "/api/call"
    }

    @ApiOperation("Returns all calls")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_CALL)
    fun getCalls(pageable: Pageable): Page<CallDTO>

    @ApiOperation("Returns all calls` id name pair")
    @GetMapping("$ENDPOINT_API_CALL/list")
    fun listCalls(@RequestParam(required = false) status: CallStatus?): List<IdNamePairDTO>

    @ApiOperation("Returns all published calls")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_API_CALL/published")
    fun getPublishedCalls(pageable: Pageable): Page<CallDTO>

    @ApiOperation("Returns a call by call id")
    @GetMapping("$ENDPOINT_API_CALL/byId/{callId}")
    fun getCallById(@PathVariable callId: Long): CallDetailDTO

    @ApiOperation("Create a new call")
    @PostMapping(ENDPOINT_API_CALL, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createCall(@RequestBody call: CallUpdateRequestDTO): CallDetailDTO

    @ApiOperation("Update an existing call")
    @PutMapping(ENDPOINT_API_CALL, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCall(@RequestBody call: CallUpdateRequestDTO): CallDetailDTO

    @ApiOperation("Publish existing call (which is now DRAFT)")
    @PutMapping("$ENDPOINT_API_CALL/byId/{callId}/publish")
    fun publishCall(@PathVariable callId: Long): CallDTO

    @ApiOperation("Setup Call FlatRates for partner budget")
    @PutMapping("$ENDPOINT_API_CALL/byId/{callId}/flatRate", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCallFlatRateSetup(
        @PathVariable callId: Long,
        @RequestBody flatRateSetup: FlatRateSetupDTO
    ): CallDetailDTO

    @ApiOperation("Update allowed real costs for partner budget")
    @PutMapping("$ENDPOINT_API_CALL/byId/{callId}/allowedRealCosts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAllowedRealCosts(
        @PathVariable callId: Long,
        @RequestBody allowedRealCosts: AllowedRealCostsDTO
    ): AllowedRealCostsDTO

    @ApiOperation("Returns allowed real costs for partner budget")
    @GetMapping("$ENDPOINT_API_CALL/byId/{callId}/allowedRealCosts")
    fun getAllowedRealCosts(@PathVariable callId: Long): AllowedRealCostsDTO

    @ApiOperation("Setup LumpSums available for Call")
    @PutMapping("$ENDPOINT_API_CALL/byId/{callId}/lumpSum", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCallLumpSums(
        @PathVariable callId: Long,
        @RequestBody lumpSumIds: Set<Long>
    ): CallDetailDTO

    @ApiOperation("Setup UnitCosts available for Call")
    @PutMapping("$ENDPOINT_API_CALL/byId/{callId}/unitCost", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCallUnitCosts(
        @PathVariable callId: Long,
        @RequestBody unitCostIds: Set<Long>
    ): CallDetailDTO

    @ApiOperation("Update pre-submission check settings for Call")
    @PutMapping("$ENDPOINT_API_CALL/byId/{callId}/preSubmissionCheck", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePreSubmissionCheckSettings(
        @PathVariable callId: Long,
        @RequestBody pluginKey: String?
    ): CallDetailDTO

}
