package io.cloudflight.jems.server.project.controller.report.project.verification

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.project.report.project.verification.ProjectReportVerificationCertificateApi
import io.cloudflight.jems.server.common.toResponseFile
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.download.DownloadProjectReportVerificationCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.generate.GenerateVerificationCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.list.ListProjectReportVerificationCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.updateDescription.UpdateDescriptionProjectReportVerificationCertificateInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
        downloadProjectReportVerificationCertificate.download(projectId = projectId, reportId = reportId, fileId = fileId).toResponseFile()

    override fun generate(projectId: Long, reportId: Long, pluginKey: String) =
        generateVerificationCertificate.generateCertificate(projectId, reportId, pluginKey)

}
