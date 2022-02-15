package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportSummaryDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Partner Report")
interface ProjectPartnerReportApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT = "/api/project/report"
    }

    @ApiOperation("Returns all project partner report summaries by partner id and version")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT/partner/byPartnerId/{partnerId}")
    fun getProjectPartnerReports(
        @PathVariable partnerId: Long,
        pageable: Pageable,
    ): Page<ProjectPartnerReportSummaryDTO>

    @ApiOperation("Returns project partner report detail")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT/partner/byPartnerId/{partnerId}/byReportId/{reportId}")
    fun getProjectPartnerReport(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): ProjectPartnerReportDTO

    @ApiOperation("Creates new partner report")
    @PostMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT/partner/create/{partnerId}")
    fun createProjectPartnerReport(
        @PathVariable partnerId: Long,
    ): ProjectPartnerReportSummaryDTO

    @ApiOperation("Submit and lock partner report")
    @PostMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT/partner/submit/{partnerId}/{reportId}")
    fun submitProjectPartnerReport(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): ProjectPartnerReportSummaryDTO

    @ApiOperation("Returns all project partners for reporting")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT/listPartners/byProjectId/{projectId}")
    fun getProjectPartnersForReporting(
        @PathVariable projectId: Long,
        sort: Sort,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectPartnerSummaryDTO>

}
