package io.cloudflight.jems.server.project.controller.report.project.verification

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.report.project.verification.ProjectReportVerificationFileApi
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import io.cloudflight.jems.server.project.service.report.project.verification.file.delete.DeleteProjectReportVerificationFileInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.file.download.DownloadProjectReportVerificationFileInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.file.list.ListProjectReportVerificationFileInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.file.updateDescription.UpdateDescriptionProjectReportVerificationFileInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.file.upload.UploadProjectReportVerificationFileInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
class ProjectReportVerificationFileController(
    private val listProjectReportVerificationFile: ListProjectReportVerificationFileInteractor,
    private val uploadProjectReportVerificationFile: UploadProjectReportVerificationFileInteractor,
    private val updateDescriptionProjectReportVerificationFile: UpdateDescriptionProjectReportVerificationFileInteractor,
    private val downloadProjectReportVerificationFile: DownloadProjectReportVerificationFileInteractor,
    private val deleteProjectReportVerificationFile: DeleteProjectReportVerificationFileInteractor,
) : ProjectReportVerificationFileApi {

    override fun list(projectId: Long, reportId: Long, pageable: Pageable): Page<JemsFileDTO> =
        listProjectReportVerificationFile.list(projectId = projectId, reportId = reportId, pageable = pageable)
            .map { it.toDto() }

    override fun upload(projectId: Long, reportId: Long, file: MultipartFile): JemsFileMetadataDTO =
        uploadProjectReportVerificationFile
            .upload(projectId = projectId, reportId = reportId, file = file.toProjectFile())
            .toDto()

    override fun updateDescription(projectId: Long, reportId: Long, fileId: Long, description: String?) =
        updateDescriptionProjectReportVerificationFile.updateDescription(
            projectId = projectId,
            reportId = reportId,
            fileId = fileId,
            description = description ?: ""
        )

    override fun download(projectId: Long, reportId: Long, fileId: Long): ResponseEntity<ByteArrayResource> =
        with(downloadProjectReportVerificationFile.download(projectId = projectId, reportId = reportId, fileId = fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }

    override fun delete(projectId: Long, reportId: Long, fileId: Long) =
        deleteProjectReportVerificationFile.delete(projectId = projectId, reportId = reportId, fileId = fileId)
}
