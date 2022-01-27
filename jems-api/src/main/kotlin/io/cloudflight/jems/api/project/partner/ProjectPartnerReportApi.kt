package io.cloudflight.jems.api.project.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerReportDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Partner Report")
@RequestMapping("/api/project/partner/report")
interface ProjectPartnerReportApi {

    @ApiOperation("Returns all project partner reports by partner id and version")
    @GetMapping("/byPartnerId/{partnerId}")
    fun getProjectPartnerReports(
        @PathVariable partnerId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectPartnerReportDTO>

    @ApiOperation("Returns all project partners for reporting")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping("/byProjectId/{projectId}/reporting")
    fun getProjectPartnersForReporting(@PathVariable projectId: Long,
                                       sort: Sort,
                                       @RequestParam(required = false) version: String? = null):
        List<ProjectPartnerSummaryDTO>

}
