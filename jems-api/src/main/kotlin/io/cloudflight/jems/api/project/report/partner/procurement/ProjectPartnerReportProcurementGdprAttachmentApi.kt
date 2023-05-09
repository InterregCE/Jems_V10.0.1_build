package io.cloudflight.jems.api.project.report.partner.procurement

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.attachment.ProjectReportProcurementFileDTO
import io.cloudflight.jems.api.project.report.partner.ProjectPartnerReportApi
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile


@Api("Project Partner Report Procurement GDPR Attachment")
interface ProjectPartnerReportProcurementGdprAttachmentApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT =
            "${ProjectPartnerReportApi.ENDPOINT_API_PROJECT_PARTNER_REPORT}/procurement/gdprAttachment/byPartnerId/{partnerId}/byReportId/{reportId}/byProcurementId/{procurementId}"
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_DOWNLOAD =
            "${ProjectPartnerReportApi.ENDPOINT_API_PROJECT_PARTNER_REPORT}/procurement/gdprAttachment/byPartnerId/{partnerId}/"
    }

    @ApiOperation("Returns all GDPR attachments for project partner report procurement")
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT)
    fun getAttachments(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
    ): List<ProjectReportProcurementFileDTO>

    @ApiOperation("Download procurement gdpr file from partner report")
    @GetMapping(
        "${ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_DOWNLOAD}/byFileId/{fileId}/download",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun downloadProcurementGdprFile(
        @PathVariable partnerId: Long,
        @PathVariable fileId: Long,
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Upload GDPR file to project partner report procurement")
    @PostMapping(
        ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT,
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun uploadAttachment(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
        @RequestPart("file") file: MultipartFile,
    ): JemsFileMetadataDTO

    @ApiOperation("Update description for GDPR procurement file")
    @PutMapping(
        "${ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT}/byFileId/{fileId}/description",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun updateReportGdprFileDescription(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
        @PathVariable fileId: Long,
        @RequestBody(required = false) description: String?,
    )

}
