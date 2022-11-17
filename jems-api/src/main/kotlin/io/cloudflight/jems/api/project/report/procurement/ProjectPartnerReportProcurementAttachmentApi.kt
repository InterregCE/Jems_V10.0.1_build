package io.cloudflight.jems.api.project.report.procurement

import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.attachment.ProjectReportProcurementFileDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Api("Project Partner Report Procurement Attachment")
interface ProjectPartnerReportProcurementAttachmentApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT =
            "$ENDPOINT_API_PROJECT_PARTNER_REPORT/procurement/attachment/byPartnerId/{partnerId}/byReportId/{reportId}/byProcurementId/{procurementId}"
    }

    @ApiOperation("Returns all attachments for project partner report procurement")
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT)
    fun getAttachments(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
    ): List<ProjectReportProcurementFileDTO>

    @ApiOperation("Upload file to project partner report procurement")
    @PostMapping(
        ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT,
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun uploadAttachment(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
        @RequestPart("file") file: MultipartFile,
    ): ProjectReportFileMetadataDTO

}
