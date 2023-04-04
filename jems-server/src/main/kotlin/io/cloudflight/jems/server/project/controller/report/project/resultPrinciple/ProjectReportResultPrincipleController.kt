package io.cloudflight.jems.server.project.controller.report.project.resultPrinciple

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.project.projectResults.ProjectReportResultPrincipleDTO
import io.cloudflight.jems.api.project.dto.report.project.projectResults.UpdateProjectReportResultPrincipleDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportResultPrincipleApi
import io.cloudflight.jems.server.project.controller.report.project.toProjectFile
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.delete.DeleteAttachmentFromProjectReportResultPrincipleInteractor
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.download.DownloadAttachmentFromProjectReportResultPrincipleInteractor
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.upload.UploadAttachmentToProjectReportResultPrincipleInteractor
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.getResultPrinciple.GetProjectReportResultPrincipleInteractor
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.updateResultPrinciple.UpdateProjectReportResultPrincipleInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectReportResultPrincipleController(
    private val getResultPrinciple: GetProjectReportResultPrincipleInteractor,
    private val updateResultPrinciple: UpdateProjectReportResultPrincipleInteractor,
    private val uploadAttachment: UploadAttachmentToProjectReportResultPrincipleInteractor,
    private val downloadAttachment: DownloadAttachmentFromProjectReportResultPrincipleInteractor,
    private val deleteAttachment: DeleteAttachmentFromProjectReportResultPrincipleInteractor,
) : ProjectReportResultPrincipleApi {

    override fun getResultAndPrinciple(projectId: Long, reportId: Long): ProjectReportResultPrincipleDTO =
        getResultPrinciple.get(projectId, reportId).toDto()

    override fun updateResultAndPrinciple(
        projectId: Long,
        reportId: Long,
        resultPrinciple: UpdateProjectReportResultPrincipleDTO
    ): ProjectReportResultPrincipleDTO = updateResultPrinciple.update(projectId, reportId, resultPrinciple.toModel()).toDto()

    override fun uploadAttachmentToResult(projectId: Long, reportId: Long, resultNumber: Int, file: MultipartFile): JemsFileMetadataDTO =
        uploadAttachment.upload(projectId, reportId, resultNumber, file = file.toProjectFile()).toDto()

    override fun deleteAttachmentFromResult(projectId: Long, reportId: Long, resultNumber: Int) =
        deleteAttachment.delete(projectId, reportId, resultNumber)

    override fun downloadAttachmentFromResult(
        projectId: Long,
        reportId: Long,
        resultNumber: Int,
    ): ResponseEntity<ByteArrayResource> = with(downloadAttachment.download(projectId, reportId, resultNumber)) {
        ResponseEntity.ok()
            .contentLength(this.second.size.toLong())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
            .body(ByteArrayResource(this.second))
    }
}
