package io.cloudflight.jems.server.project.controller.report.partner.control.file

import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.report.partner.control.ProjectPartnerControlReportFileApi
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import io.cloudflight.jems.server.project.service.report.partner.control.file.deleteReportControlCertificateAttachment.DeleteReportControlCertificateAttachmentInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificate.DownloadReportControlCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificateAttachment.DownloadReportControlCertificateAttachmentInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate.GenerateReportControlCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.listCertificates.ListReportControlCertificatesInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToCertificate.SetDescriptionToCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.uploadFileToCertificate.UploadFileToCertificateInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectPartnerControlReportFileController(
    private val generateReportControlCertificate: GenerateReportControlCertificateInteractor,
    private val setCertificateFileDescription: SetDescriptionToCertificateInteractor,
    private val listReportControlCertificates: ListReportControlCertificatesInteractor,
    private val downloadReportControlCertificate: DownloadReportControlCertificateInteractor,
    private val deleteReportControlCertificateAttachment: DeleteReportControlCertificateAttachmentInteractor,
    private val downloadReportControlCertificateAttachment: DownloadReportControlCertificateAttachmentInteractor,
    private val uploadFileToCertificate: UploadFileToCertificateInteractor
): ProjectPartnerControlReportFileApi  {

    override fun generateControlReportCertificate(partnerId: Long, reportId: Long) =
        generateReportControlCertificate.generateCertificate(partnerId, reportId)


    override fun updateControlReportCertificateFileDescription(
        partnerId: Long,
        reportId: Long,
        fileId: Long,
        description: String?
    ) = setCertificateFileDescription.setDescription(partnerId, reportId, fileId, description ?: "")


    override fun listFiles(
        partnerId: Long,
        reportId: Long,
        pageable: Pageable
    ) = listReportControlCertificates.list(
        partnerId = partnerId,
        reportId = reportId,
        pageable = pageable
    ).map { it.toDto() }

    override fun downloadControlReportCertificate(
        partnerId: Long,
        reportId: Long,
        fileId: Long
    ): ResponseEntity<ByteArrayResource> {
        return with(downloadReportControlCertificate.downloadReportControlCertificate(partnerId = partnerId, reportId = reportId, fileId = fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }
    }

    override fun downloadControlReportAttachment(
        partnerId: Long,
        reportId: Long,
        fileId: Long
    ): ResponseEntity<ByteArrayResource> {
        return with(downloadReportControlCertificateAttachment.downloadReportControlCertificateAttachment(
            partnerId = partnerId,
            reportId = reportId,
            fileId = fileId
        )) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }
    }

    override fun deleteControlReportAttachment(partnerId: Long, reportId: Long, fileId: Long, attachmentId: Long) {
        deleteReportControlCertificateAttachment.deleteReportControlCertificateAttachment(
            partnerId = partnerId,
            reportId = reportId,
            fileId = fileId,
            attachmentId = attachmentId
        )
    }

    override fun uploadReportCertificateSigned(partnerId: Long, reportId: Long, fileId: Long, file: MultipartFile): ProjectReportFileMetadataDTO {
        return uploadFileToCertificate
            .uploadToCertificate(partnerId, reportId, fileId = fileId, file.toProjectFile()).toDto()
    }
}
