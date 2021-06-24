package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectFileApi
import io.cloudflight.jems.api.project.dto.file.InputProjectFileDescription
import io.cloudflight.jems.api.project.dto.file.OutputProjectFile
import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectApplicationFile
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectApplicationFiles
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectAssessmentFile
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectAssessmentFiles
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectApplicationFile
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectAssessmentFile
import io.cloudflight.jems.server.project.entity.file.FileMetadata
import io.cloudflight.jems.server.project.service.file.FileStorageService
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
    private val fileStorageService: FileStorageService
) : ProjectFileApi {

    @CanUpdateProjectAssessmentFile
    override fun uploadProjectAssessmentFile(projectId: Long, file: MultipartFile) {
        uploadFile(projectId, file, ProjectFileType.ASSESSMENT_FILE)
    }

    @CanRetrieveProjectAssessmentFile
    override fun downloadProjectAssessmentFile(projectId: Long, fileId: Long): ResponseEntity<ByteArrayResource> {
        val data = fileStorageService.downloadFile(projectId, fileId, ProjectFileType.ASSESSMENT_FILE)
        return ResponseEntity.ok()
            .contentLength(data.second.size.toLong())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${data.first}\"")
            .body(ByteArrayResource(data.second))
    }

    @CanRetrieveProjectAssessmentFiles
    override fun getAssessmentFilesForProject(
        projectId: Long,
        pageable: Pageable
    ): Page<OutputProjectFile> =
        fileStorageService.getFilesForProject(projectId, ProjectFileType.ASSESSMENT_FILE, pageable)

    @CanUpdateProjectAssessmentFile
    override fun setDescriptionToProjectAssessmentFile(
        projectId: Long,
        fileId: Long,
        projectFileDescription: InputProjectFileDescription
    ): OutputProjectFile =
        fileStorageService.setDescription(projectId, fileId, ProjectFileType.ASSESSMENT_FILE, projectFileDescription.description)

    @CanUpdateProjectAssessmentFile
    override fun deleteProjectAssessmentFile(projectId: Long, fileId: Long) =
        fileStorageService.deleteFile(projectId, fileId, ProjectFileType.ASSESSMENT_FILE)

    @CanUpdateProjectApplicationFile
    override fun uploadProjectApplicationFile(projectId: Long, file: MultipartFile) {
        uploadFile(projectId, file, ProjectFileType.APPLICANT_FILE)
    }

    @CanRetrieveProjectApplicationFile
    override fun downloadProjectApplicationFile(projectId: Long, fileId: Long): ResponseEntity<ByteArrayResource> {
        val data = fileStorageService.downloadFile(projectId, fileId, ProjectFileType.APPLICANT_FILE)
        return ResponseEntity.ok()
            .contentLength(data.second.size.toLong())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${data.first}\"")
            .body(ByteArrayResource(data.second))
    }

    @CanRetrieveProjectApplicationFiles
    override fun getApplicationFilesForProject(
        projectId: Long,
        pageable: Pageable
    ): Page<OutputProjectFile> =
        fileStorageService.getFilesForProject(projectId, ProjectFileType.APPLICANT_FILE, pageable)

    @CanUpdateProjectApplicationFile
    override fun setDescriptionToProjectApplicationFile(
        projectId: Long,
        fileId: Long,
        projectFileDescription: InputProjectFileDescription
    ): OutputProjectFile =
        fileStorageService.setDescription(projectId, fileId, ProjectFileType.APPLICANT_FILE, projectFileDescription.description)

    @CanUpdateProjectApplicationFile
    override fun deleteProjectApplicationFile(projectId: Long, fileId: Long) =
        fileStorageService.deleteFile(projectId, fileId, ProjectFileType.APPLICANT_FILE)

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
