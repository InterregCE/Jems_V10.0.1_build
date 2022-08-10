package io.cloudflight.jems.api.project.report.procurement

import io.cloudflight.jems.api.project.dto.report.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Partner Report Procurement Beneficial Owner")
interface ProjectPartnerReportProcurementBeneficialOwnerApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_BO =
            "$ENDPOINT_API_PROJECT_PARTNER_REPORT/procurement/beneficialOwner/byPartnerId/{partnerId}/byReportId/{reportId}/byProcurementId/{procurementId}"
    }

    @ApiOperation("Returns all beneficial owners for project partner report procurement")
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_BO)
    fun getBeneficialOwners(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
    ): List<ProjectPartnerReportProcurementBeneficialDTO>

    @ApiOperation("Updates beneficial owners of project partner report procurement")
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_BO, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBeneficialOwners(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
        @RequestBody owners: List<ProjectPartnerReportProcurementBeneficialChangeDTO>,
    ): List<ProjectPartnerReportProcurementBeneficialDTO>

}
