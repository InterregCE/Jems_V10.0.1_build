package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectFileApi
import io.cloudflight.jems.api.project.dto.file.ProjectFileCategoryDTO
import io.cloudflight.jems.api.project.dto.file.ProjectFileMetadataDTO
import io.cloudflight.jems.server.project.service.file.delete_project_file.DeleteProjectFileInteractor
import io.cloudflight.jems.server.project.service.file.download_project_file.DownloadProjectFileInteractor
import io.cloudflight.jems.server.project.service.file.list_project_file_metadata.ListProjectFileMetadataInteractor
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.set_project_file_description.SetProjectFileDescriptionInteractor
import io.cloudflight.jems.server.project.service.file.upload_project_file.UploadProjectFileInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectFileController(
    private val uploadProjectFile: UploadProjectFileInteractor,
    private val downloadProjectFile: DownloadProjectFileInteractor,
    private val deleteProjectFile: DeleteProjectFileInteractor,
    private val listProjectFileMetadata: ListProjectFileMetadataInteractor,
    private val setProjectFileDescription: SetProjectFileDescriptionInteractor
) : ProjectFileApi {

    override fun uploadFile(projectId: Long, fileCategory: ProjectFileCategoryDTO, file: MultipartFile) =
        uploadProjectFile.upload(
            projectId,
            fileCategory.toModel(),
            ProjectFile(file.inputStream, file.originalFilename ?: file.name, file.size)
        ).toDTO()

    override fun listProjectFiles(
        projectId: Long, fileCategory: ProjectFileCategoryDTO, pageable: Pageable
    ): Page<ProjectFileMetadataDTO> =
        listProjectFileMetadata.list(projectId, fileCategory.toModel(), pageable).toDTO()

    override fun setProjectFileDescription(projectId: Long, fileId: Long, description: String?) =
        setProjectFileDescription.setDescription(projectId, fileId, description).toDTO()

    override fun deleteProjectFile(projectId: Long, fileId: Long) =
        deleteProjectFile.delete(projectId, fileId)

    override fun downloadFile(projectId: Long, fileId: Long): ResponseEntity<ByteArrayResource> =
        with(downloadProjectFile.download(projectId, fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first.name}\"")
                .body(ByteArrayResource(this.second))
        }
}
