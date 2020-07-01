package io.cloudflight.ems.controller

import io.cloudflight.ems.api.ProjectFileApi
import io.cloudflight.ems.api.dto.InputProjectFileDescription
import io.cloudflight.ems.api.dto.OutputProjectFile
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

    @PreAuthorize("@projectAuthorization.canAccessProject(#projectId)")
    override fun uploadProjectFile(projectId: Long, file: MultipartFile) {
        fileStorageService.saveFile(
            file.inputStream,
            FileMetadata(
                name = file.originalFilename ?: file.name,
                projectId = projectId,
                size = file.size
            )
        )
    }

    @PreAuthorize("@projectAuthorization.canAccessProject(#projectId)")
    override fun downloadFile(projectId: Long, fileId: Long): ResponseEntity<ByteArrayResource> {
        val data = fileStorageService.downloadFile(projectId, fileId)
        return ResponseEntity.ok()
            .contentLength(data.second.size.toLong())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${data.first}\"")
            .body(ByteArrayResource(data.second))
    }

    @PreAuthorize("@projectAuthorization.canAccessProject(#projectId)")
    override fun getFilesForProject(projectId: Long, pageable: Pageable): Page<OutputProjectFile> {
        return fileStorageService.getFilesForProject(projectId, pageable)
    }

    @PreAuthorize("@projectAuthorization.canAccessProject(#projectId)")
    override fun setDescriptionToFile(
        projectId: Long,
        fileId: Long,
        projectFileDescription: InputProjectFileDescription
    ): OutputProjectFile {
        return fileStorageService.setDescription(projectId, fileId, projectFileDescription.description)
    }

    @PreAuthorize("@projectAuthorization.canAccessProject(#projectId)")
    override fun deleteFile(projectId: Long, fileId: Long) {
        fileStorageService.deleteFile(projectId, fileId)
    }
}
