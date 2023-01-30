package io.cloudflight.jems.api.project.report.partner.control

import io.cloudflight.jems.api.project.dto.report.file.PartnerReportControlFileDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileDTO
import io.cloudflight.jems.api.project.report.partner.ProjectPartnerReportApi
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("ProjectPartnerControl Report File API")
interface ProjectPartnerControlReportFileApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL_FILE =
            "${ProjectPartnerReportApi.ENDPOINT_API_PROJECT_PARTNER_CONTROL_REPORT}/file/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Generate certificate file for report control")
    @GetMapping("${ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL_FILE}/generateCertificate")
    fun generateControlReportCertificate(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    )

    @ApiOperation("Update description of generated control report certificate file")
    @PutMapping(
        "${ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL_FILE}/byFileId/{fileId}/description",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun updateControlReportCertificateFileDescription(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable fileId: Long,
        @RequestBody(required = false) description: String?
    )

    @ApiOperation("List control report certificates")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("${ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL_FILE}/list")
    fun listFiles(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        pageable: Pageable
    ): Page<PartnerReportControlFileDTO>

    @ApiOperation("Download control report certificate")
    @GetMapping(
        "${ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL_FILE}/download/{fileId}",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun downloadControlReportCertificate(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable fileId: Long
    ): ResponseEntity<ByteArrayResource>
}
