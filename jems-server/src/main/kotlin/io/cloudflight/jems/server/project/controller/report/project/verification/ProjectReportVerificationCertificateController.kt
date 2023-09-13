package io.cloudflight.jems.server.project.controller.report.project.verification

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.project.report.project.verification.ProjectReportVerificationCertificateApi
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.download.DownloadProjectReportVerificationCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.generate.GenerateVerificationCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.list.ListProjectReportVerificationCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.updateDescription.UpdateDescriptionProjectReportVerificationCertificateInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController


@RestController
class ProjectReportVerificationCertificateController(
    private val listProjectReportVerificationCertificate: ListProjectReportVerificationCertificateInteractor,
    private val updateDescriptionProjectReportVerificationCertificate: UpdateDescriptionProjectReportVerificationCertificateInteractor,
    private val downloadProjectReportVerificationCertificate: DownloadProjectReportVerificationCertificateInteractor,
    private val generateVerificationCertificate: GenerateVerificationCertificateInteractor,
) : ProjectReportVerificationCertificateApi {

    override fun list(projectId: Long, reportId: Long, pageable: Pageable): Page<JemsFileDTO> =
        listProjectReportVerificationCertificate.list(projectId = projectId, reportId = reportId, pageable = pageable)
            .map { it.toDto() }

    override fun updateDescription(projectId: Long, reportId: Long, fileId: Long, description: String?) =
        updateDescriptionProjectReportVerificationCertificate.updateDescription(
            projectId = projectId,
            reportId = reportId,
            fileId = fileId,
            description = description ?: ""
        )

    override fun download(projectId: Long, reportId: Long, fileId: Long): ResponseEntity<ByteArrayResource> =
        with(downloadProjectReportVerificationCertificate.download(projectId = projectId, reportId = reportId, fileId = fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }

    override fun generate(projectId: Long, reportId: Long, pluginKey: String) =
        generateVerificationCertificate.generateCertificate(projectId, reportId, pluginKey)

}
