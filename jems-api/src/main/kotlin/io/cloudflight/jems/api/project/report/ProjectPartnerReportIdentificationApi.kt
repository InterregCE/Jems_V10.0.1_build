package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportPeriodDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.UpdateProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ProjectPartnerControlReportChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ProjectPartnerControlReportDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_CONTROL_REPORT
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Partner Report Identification")
interface ProjectPartnerReportIdentificationApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_IDENTIFICATION =
            "$ENDPOINT_API_PROJECT_PARTNER_REPORT/identification/byPartnerId/{partnerId}/byReportId/{reportId}"

        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL =
            "$ENDPOINT_API_PROJECT_PARTNER_CONTROL_REPORT/identification/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Returns project partner report identification")
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_IDENTIFICATION)
    fun getIdentification(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): ProjectPartnerReportIdentificationDTO

    @ApiOperation("Updates project partner report identification")
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_IDENTIFICATION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateIdentification(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody identification: UpdateProjectPartnerReportIdentificationDTO
    ): ProjectPartnerReportIdentificationDTO

    @ApiOperation("Returns all periods from AF available for this report")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT_IDENTIFICATION/periods")
    fun getAvailablePeriods(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectPartnerReportPeriodDTO>

    @ApiOperation("Returns project partner control report identification")
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL)
    fun getControlIdentification(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): ProjectPartnerControlReportDTO

    @ApiOperation("Updates project partner control report identification")
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateControlIdentification(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody identification: ProjectPartnerControlReportChangeDTO,
    ): ProjectPartnerControlReportDTO

}
