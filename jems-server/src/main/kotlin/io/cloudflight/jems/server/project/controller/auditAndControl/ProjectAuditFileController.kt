package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.auditAndControl.ProjectAuditAndControlFileApi
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import io.cloudflight.jems.server.project.service.auditAndControl.file.delete.DeleteAuditControlFileInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.file.download.DownloadAuditControlFileInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.file.list.ListAuditControlFileInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.file.updateDescription.UpdateDescriptionAuditControlFileInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.file.upload.UploadAuditControlFileInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectAuditFileController(
    private val listAuditControlFileInteractor: ListAuditControlFileInteractor,
    private val uploadAuditControlFileInteractor: UploadAuditControlFileInteractor,
    private val updateDescriptionAuditControlFileInteractor: UpdateDescriptionAuditControlFileInteractor,
    private val downloadAuditControlFileInteractor: DownloadAuditControlFileInteractor,
    private val deleteAuditControlFileInteractor: DeleteAuditControlFileInteractor,
) : ProjectAuditAndControlFileApi {

    override fun list(projectId: Long, auditControlId: Long, pageable: Pageable): Page<JemsFileDTO> =
        listAuditControlFileInteractor.list(auditControlId = auditControlId, pageable = pageable)
            .map { it.toDto() }

    override fun upload(projectId: Long, auditControlId: Long, file: MultipartFile): JemsFileMetadataDTO =
        uploadAuditControlFileInteractor.upload(auditControlId = auditControlId, file = file.toProjectFile())
            .toDto()

    override fun updateDescription(projectId: Long, auditControlId: Long, fileId: Long, description: String?) =
        updateDescriptionAuditControlFileInteractor.updateDescription(
            auditControlId = auditControlId,
            fileId = fileId,
            description = description ?: ""
        )

    override fun download(projectId: Long, auditControlId: Long, fileId: Long): ResponseEntity<ByteArrayResource> =
        with(downloadAuditControlFileInteractor.download(projectId = projectId, auditControlId = auditControlId, fileId = fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }

    override fun delete(projectId: Long, auditControlId: Long, fileId: Long) =
        deleteAuditControlFileInteractor.delete(projectId = projectId, auditControlId = auditControlId, fileId = fileId)

}
