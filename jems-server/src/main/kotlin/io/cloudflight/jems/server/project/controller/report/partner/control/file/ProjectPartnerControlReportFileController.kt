package io.cloudflight.jems.server.project.controller.report.partner.control.file

import io.cloudflight.jems.api.project.report.partner.control.ProjectPartnerControlReportFileApi
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificate.DownloadReportControlCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate.GenerateReportControlCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.listCertificates.ListReportControlCertificatesInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToCertificate.SetDescriptionToCertificateInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerControlReportFileController(
    private val generateReportControlCertificate: GenerateReportControlCertificateInteractor,
    private val setCertificateFileDescription: SetDescriptionToCertificateInteractor,
    private val listReportControlCertificates: ListReportControlCertificatesInteractor,
    private val downloadReportControlCertificate: DownloadReportControlCertificateInteractor
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
}
