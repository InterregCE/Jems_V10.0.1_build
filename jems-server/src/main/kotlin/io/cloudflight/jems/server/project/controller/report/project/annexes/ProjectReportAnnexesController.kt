package io.cloudflight.jems.server.project.controller.report.project.annexes

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileSearchRequestDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportAnnexesApi
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toModel
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import io.cloudflight.jems.server.project.service.report.project.annexes.delete.DeleteProjectReportAnnexesFileInteractor
import io.cloudflight.jems.server.project.service.report.project.annexes.download.DownloadProjectReportAnnexesFileInteractor
import io.cloudflight.jems.server.project.service.report.project.annexes.list.ListProjectReportAnnexesInteractor
import io.cloudflight.jems.server.project.service.report.project.annexes.update.SetDescriptionToProjectReportFileInteractor
import io.cloudflight.jems.server.project.service.report.project.annexes.upload.UploadProjectReportAnnexesFileInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectReportAnnexesController(
    private val listProjectReportAnnexes: ListProjectReportAnnexesInteractor,
    private val uploadProjectReportAnnexesFile: UploadProjectReportAnnexesFileInteractor,
    private val setDescriptionToProjectReportFile: SetDescriptionToProjectReportFileInteractor,
    private val deleteProjectReportAnnexesFile: DeleteProjectReportAnnexesFileInteractor,
    private val downloadProjectReportAnnexesFile: DownloadProjectReportAnnexesFileInteractor
) : ProjectReportAnnexesApi {

    override fun getProjectReportAnnexes(
        projectId: Long,
        reportId: Long,
        pageable: Pageable,
        searchRequest: ProjectReportFileSearchRequestDTO
    ): Page<JemsFileDTO> =
        listProjectReportAnnexes.list(
            projectId,
            reportId,
            pageable,
            searchRequest.toModel()
        ).map { it.toDto() }

    override fun uploadProjectReportAnnexesFile(
        projectId: Long,
        reportId: Long,
        file: MultipartFile
    ): JemsFileMetadataDTO =
        uploadProjectReportAnnexesFile.upload(
            projectId,
            reportId,
            file.toProjectFile()
        ).toDto()

    override fun updateProjectReportAnnexesFileDescription(
        projectId: Long,
        reportId: Long,
        fileId: Long,
        description: String?
    ) {
        setDescriptionToProjectReportFile.update(
            projectId,
            reportId,
            fileId,
            description = description ?: ""
        )
    }

    override fun deleteProjectReportAnnexesFile(projectId: Long, reportId: Long, fileId: Long) {
        deleteProjectReportAnnexesFile.delete(
            projectId,
            reportId,
            fileId
        )
    }

    override fun downloadProjectReportAnnexesFile(
        projectId: Long,
        reportId: Long,
        fileId: Long
    ): ResponseEntity<ByteArrayResource> =
        with(downloadProjectReportAnnexesFile.download(projectId, reportId, fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }
}
