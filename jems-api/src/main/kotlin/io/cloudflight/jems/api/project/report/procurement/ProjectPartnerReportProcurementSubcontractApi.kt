package io.cloudflight.jems.api.project.report.procurement

import io.cloudflight.jems.api.project.dto.report.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontractDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Partner Report Procurement Subcontractor")
interface ProjectPartnerReportProcurementSubcontractApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_SC =
            "$ENDPOINT_API_PROJECT_PARTNER_REPORT/procurement/subcontractor/byPartnerId/{partnerId}/byReportId/{reportId}/byProcurementId/{procurementId}"
    }

    @ApiOperation("Returns all subcontractors for project partner report procurement")
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_SC)
    fun getSubcontractors(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
    ): List<ProjectPartnerReportProcurementSubcontractDTO>

    @ApiOperation("Updates subcontractors of project partner report procurement")
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_SC, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateSubcontractors(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
        @RequestBody subcontracts: List<ProjectPartnerReportProcurementSubcontractChangeDTO>,
    ): List<ProjectPartnerReportProcurementSubcontractDTO>

}
