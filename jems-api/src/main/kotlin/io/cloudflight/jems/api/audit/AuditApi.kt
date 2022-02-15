package io.cloudflight.jems.api.audit

import io.cloudflight.jems.api.audit.dto.AuditDTO
import io.cloudflight.jems.api.audit.dto.AuditSearchRequestDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Audit")
interface AuditApi {

    companion object {
        private const val ENDPOINT_API_AUDIT = "/api/audit"
    }

    @ApiOperation("Search for audit events")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @PostMapping(ENDPOINT_API_AUDIT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getAudits(pageable: Pageable, @RequestBody(required = false) searchRequest: AuditSearchRequestDTO? = null): Page<AuditDTO>

}
