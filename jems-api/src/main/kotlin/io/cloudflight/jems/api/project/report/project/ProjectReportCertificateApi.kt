package io.cloudflight.jems.api.project.report.project

import io.cloudflight.jems.api.project.dto.report.project.certificate.PartnerReportCertificateDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportApi.Companion.ENDPOINT_API_PROJECT_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping

@Api("Project Report Certificate")
interface ProjectReportCertificateApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT_CERTIFICATE = "$ENDPOINT_API_PROJECT_REPORT/certificate"
    }

    @ApiOperation("Returns all report certificates")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_CERTIFICATE)
    fun getProjectReportListOfCertificate(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        pageable: Pageable,
    ): Page<PartnerReportCertificateDTO>

    @ApiOperation("Deselect certificate")
    @PutMapping("$ENDPOINT_API_PROJECT_REPORT_CERTIFICATE/deselect/{certificateId}")
    fun deselectCertificate(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable certificateId: Long,
    )

    @ApiOperation("Select certificate")
    @PutMapping("$ENDPOINT_API_PROJECT_REPORT_CERTIFICATE/select/{certificateId}")
    fun selectCertificate(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable certificateId: Long,
    )

}
