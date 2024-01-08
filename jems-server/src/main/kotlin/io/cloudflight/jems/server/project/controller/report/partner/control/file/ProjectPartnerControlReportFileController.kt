package io.cloudflight.jems.server.project.controller.report.partner.control.file

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.report.partner.control.ProjectPartnerControlReportFileApi
import io.cloudflight.jems.server.common.toResponseFile
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import io.cloudflight.jems.server.project.service.report.partner.control.file.deleteFileAttachment.DeleteReportControlFileAttachmentInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadFile.DownloadReportControlFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.downloadFileAttachment.DownloadReportControlFileAttachmentInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate.GenerateReportControlCertificateInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport.GenerateReportControlExportInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.listFiles.ListReportControlFilesInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToFile.SetDescriptionToFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.uploadAttachmentToFile.UploadAttachmentToFileInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectPartnerControlReportFileController(
    private val generateReportControlCertificate: GenerateReportControlCertificateInteractor,
    private val generateReportControlExport: GenerateReportControlExportInteractor,
    private val setReportControlFileDescription: SetDescriptionToFileInteractor,
    private val listReportControlFiles: ListReportControlFilesInteractor,
    private val downloadReportControlFile: DownloadReportControlFileInteractor,
    private val deleteReportControlFileAttachment: DeleteReportControlFileAttachmentInteractor,
    private val downloadReportControlFileAttachment: DownloadReportControlFileAttachmentInteractor,
    private val uploadFileToControlfile: UploadAttachmentToFileInteractor
): ProjectPartnerControlReportFileApi  {

    override fun generateControlReportCertificate(partnerId: Long, reportId: Long, pluginKey: String) =
        generateReportControlCertificate.generateCertificate(partnerId, reportId, pluginKey)


    override fun generateControlReportExport(partnerId: Long, reportId: Long, pluginKey: String) =
        generateReportControlExport.export(partnerId, reportId, pluginKey)


    override fun updateControlReportFileDescription(
        partnerId: Long,
        reportId: Long,
        fileId: Long,
        description: String?
    ) = setReportControlFileDescription.setDescription(partnerId, reportId, fileId, description ?: "")

    override fun listFiles(
        partnerId: Long,
        reportId: Long,
        pageable: Pageable
    ) = listReportControlFiles.list(
        partnerId = partnerId,
        reportId = reportId,
        pageable = pageable
    ).map { it.toDto() }

    override fun downloadControlReportFile(
        partnerId: Long,
        reportId: Long,
        fileId: Long
    ): ResponseEntity<ByteArrayResource> {
        return downloadReportControlFile.download(partnerId = partnerId, reportId = reportId, fileId = fileId).toResponseFile()
    }

    override fun downloadControlReportAttachment(
        partnerId: Long,
        reportId: Long,
        fileId: Long
    ): ResponseEntity<ByteArrayResource> =
        downloadReportControlFileAttachment.downloadReportControlCertificateAttachment(
            partnerId = partnerId,
            reportId = reportId,
            fileId = fileId
        ).toResponseFile()

    override fun deleteControlReportAttachment(partnerId: Long, reportId: Long, fileId: Long, attachmentId: Long) {
        deleteReportControlFileAttachment.deleteReportControlCertificateAttachment(
            partnerId = partnerId,
            reportId = reportId,
            fileId = fileId,
            attachmentId = attachmentId
        )
    }

    override fun uploadReportCertificateSigned(partnerId: Long, reportId: Long, fileId: Long, file: MultipartFile): JemsFileMetadataDTO {
        return uploadFileToControlfile
            .uploadAttachment(partnerId, reportId, fileId = fileId, file.toProjectFile()).toDto()
    }
}
