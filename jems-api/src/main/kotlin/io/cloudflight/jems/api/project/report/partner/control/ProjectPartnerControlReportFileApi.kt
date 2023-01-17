package io.cloudflight.jems.api.project.report.partner.control

import io.cloudflight.jems.api.project.report.partner.ProjectPartnerReportApi
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


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

}