package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.UpdateProjectPartnerReportProcurementDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Partner Report Procurement")
interface ProjectPartnerReportProcurementApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT =
            "$ENDPOINT_API_PROJECT_PARTNER_REPORT/procurement/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Returns all project partner report procurements")
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT)
    fun getProcurement(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectPartnerReportProcurementDTO>

    @ApiOperation("Updates project partner report procurements")
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProcurement(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody procurementData: List<UpdateProjectPartnerReportProcurementDTO>,
    ): List<ProjectPartnerReportProcurementDTO>

    @ApiOperation("Returns all project partner report procurements")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT/selector")
    fun getProcurementSelectorList(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<IdNamePairDTO>

}
