package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.project.dto.report.partner.contribution.ProjectPartnerReportContributionWrapperDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.UpdateProjectPartnerReportContributionDataDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Partner Report Contribution")
interface ProjectPartnerReportContributionApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTRIBUTION =
            "$ENDPOINT_API_PROJECT_PARTNER_REPORT/contribution/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Returns all project partner report contributions")
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTRIBUTION)
    fun getContribution(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): ProjectPartnerReportContributionWrapperDTO

    @ApiOperation("Updates project partner report contributions")
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTRIBUTION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateContribution(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody contributionData: UpdateProjectPartnerReportContributionDataDTO,
    ): ProjectPartnerReportContributionWrapperDTO

}
