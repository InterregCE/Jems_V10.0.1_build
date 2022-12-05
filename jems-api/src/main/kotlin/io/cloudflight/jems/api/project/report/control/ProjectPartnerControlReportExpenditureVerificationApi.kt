package io.cloudflight.jems.api.project.report.control

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationUpdateDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_CONTROL_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Partner Report Expenditure Verification")
interface ProjectPartnerControlReportExpenditureVerificationApi {

    companion object {
        const val ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_VERIFICATION =
            "$ENDPOINT_API_PROJECT_PARTNER_CONTROL_REPORT/expenditure/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Returns all expenditure verification by partner id and report id")
    @GetMapping(ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_VERIFICATION)
    fun getProjectPartnerExpenditureVerification(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectPartnerControlReportExpenditureVerificationDTO>

    @ApiOperation("Update partner control report expenditure verification")
    @PutMapping(ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_VERIFICATION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePartnerReportExpendituresVerification(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody expenditureVerification: List<ProjectPartnerControlReportExpenditureVerificationUpdateDTO>
    ): List<ProjectPartnerControlReportExpenditureVerificationDTO>

}
