package io.cloudflight.jems.server.project.controller.report.partner.procurement.gdprAttachment

import io.cloudflight.jems.api.project.report.partner.procurement.ProjectPartnerReportProcurementGdprAttachmentApi
import io.cloudflight.jems.server.project.controller.report.partner.procurement.attachment.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.downloadProjectPartnerProcurementGdprFile.DownloadProjectPartnerReportGdprFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.getProjectPartnerReportProcurementGdprAttachment.GetProjectPartnerReportProcurementGdprAttachmentInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.setDescriptionToProjectPartnerReportProcurementGdprFile.SetDescriptionToProjectPartnerReportProcurementGdprFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.uploadFileToProjectPartnerReportProcurementGdprAttachment.UploadFileToProjectPartnerReportProcurementGdprAttachmentInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectPartnerReportProcurementGdprAttachmentController(
    private val getAttachment: GetProjectPartnerReportProcurementGdprAttachmentInteractor,
    private val downloadFile: DownloadProjectPartnerReportGdprFileInteractor,
    private val uploadFile: UploadFileToProjectPartnerReportProcurementGdprAttachmentInteractor,
    private val updateDescription: SetDescriptionToProjectPartnerReportProcurementGdprFileInteractor
) : ProjectPartnerReportProcurementGdprAttachmentApi {

    override fun getAttachments(partnerId: Long, reportId: Long, procurementId: Long) =
        getAttachment.getGdprAttachment(partnerId = partnerId, reportId = reportId, procurementId).map { it.toDto() }

    override fun downloadProcurementGdprFile(
        partnerId: Long,
        fileId: Long
    ): ResponseEntity<ByteArrayResource> =
        with(downloadFile.download(partnerId, fileId = fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }

    override fun uploadAttachment(partnerId: Long, reportId: Long, procurementId: Long, file: MultipartFile) =
        uploadFile
            .uploadToGdprProcurement(partnerId = partnerId, reportId = reportId, procurementId, file.toProjectFile())
            .toDto()

    override fun updateReportGdprFileDescription(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        fileId: Long,
        description: String?
    ) =
        updateDescription.setDescription(partnerId, reportId, fileId, procurementId, description ?: "")
}
