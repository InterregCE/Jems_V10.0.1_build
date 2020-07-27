package io.cloudflight.ems.controller

import io.cloudflight.ems.api.ProjectFileApi
import io.cloudflight.ems.api.dto.InputProjectFileDescription
import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.api.dto.ProjectFileType
import io.cloudflight.ems.dto.FileMetadata
import io.cloudflight.ems.service.FileStorageService
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectFileController(
    private val fileStorageService: FileStorageService
) : ProjectFileApi {

    @PreAuthorize("@projectFileAuthorization.canUploadFile(#projectId, 'APPLICANT_FILE')")
    override fun uploadApplicationProjectFile(projectId: Long, file: MultipartFile) {
        uploadFile(projectId, file, ProjectFileType.APPLICANT_FILE)
    }

    @PreAuthorize("@projectFileAuthorization.canUploadFile(#projectId, 'ASSESSMENT_FILE')")
    override fun uploadAssessmentProjectFile(projectId: Long, file: MultipartFile) {
        uploadFile(projectId, file, ProjectFileType.ASSESSMENT_FILE)
    }

    @PreAuthorize("@projectFileAuthorization.canDownloadFile(#projectId, #fileId)")
    override fun downloadFile(projectId: Long, fileId: Long): ResponseEntity<ByteArrayResource> {
        val data = fileStorageService.downloadFile(projectId, fileId)
        return ResponseEntity.ok()
            .contentLength(data.second.size.toLong())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${data.first}\"")
            .body(ByteArrayResource(data.second))
    }

    @PreAuthorize("@projectFileAuthorization.canListFiles(#projectId, 'APPLICANT_FILE')")
    override fun getApplicationFilesForProject(projectId: Long, pageable: Pageable): Page<OutputProjectFile> {
        return fileStorageService.getFilesForProject(projectId, ProjectFileType.APPLICANT_FILE, pageable)
    }

    @PreAuthorize("@projectFileAuthorization.canListFiles(#projectId, 'ASSESSMENT_FILE')")
    override fun getAssessmentFilesForProject(projectId: Long, pageable: Pageable): Page<OutputProjectFile> {
        return fileStorageService.getFilesForProject(projectId, ProjectFileType.ASSESSMENT_FILE, pageable)
    }

    @PreAuthorize("@projectFileAuthorization.canChangeFile(#projectId, #fileId)")
    override fun setDescriptionToFile(
        projectId: Long,
        fileId: Long,
        projectFileDescription: InputProjectFileDescription
    ): OutputProjectFile {
        return fileStorageService.setDescription(projectId, fileId, projectFileDescription.description)
    }

    @PreAuthorize("@projectFileAuthorization.canChangeFile(#projectId, #fileId)")
    override fun deleteFile(projectId: Long, fileId: Long) {
        fileStorageService.deleteFile(projectId, fileId)
    }

    private fun uploadFile(projectId: Long, file: MultipartFile, type: ProjectFileType) {
        fileStorageService.saveFile(
            file.inputStream,
            FileMetadata(
                name = file.originalFilename ?: file.name,
                projectId = projectId,
                size = file.size,
                type = type
            )
        )
    }

}
