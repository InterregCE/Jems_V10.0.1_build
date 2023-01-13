package io.cloudflight.jems.api.project.contracting.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@Api("Project Contracting Partners")
interface ContractingPartnersApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTING_PARTNERS =
            "api/project/{projectId}/contracting/partners"
    }

    @ApiOperation("Returns all project partners (only name) for contracting")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping(ENDPOINT_API_CONTRACTING_PARTNERS)
    fun getProjectPartnersForContracting(
        @PathVariable projectId: Long,
        pageable: Pageable,
        @RequestParam(required = false) version: String? = null,
    ): List<ProjectPartnerSummaryDTO>
}
