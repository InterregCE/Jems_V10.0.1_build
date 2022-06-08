package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.ProjectPartnerReportContributionWrapperDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.UpdateProjectPartnerReportContributionDataDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

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

    @ApiOperation("Upload file to contribution")
    @PostMapping(
        "$ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTRIBUTION/byContributionId/{contributionId}/file",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadFileToContribution(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable contributionId: Long,
        @RequestPart("file") file: MultipartFile,
    ): ProjectReportFileMetadataDTO

}
