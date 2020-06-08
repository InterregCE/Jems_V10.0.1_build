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
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectFileController(
    private val fileStorageService: FileStorageService
) : ProjectFileApi {

    override fun uploadProjectFile(projectId: Long, file: MultipartFile) {
        fileStorageService.saveFile(
            file.inputStream,
            FileMetadata(
                name = file.originalFilename ?: file.name,
                projectId = projectId,
                size = file.size
            ))
    }

    override fun downloadFile(projectId: Long, filename: String): ResponseEntity<ByteArrayResource> {
        val data = fileStorageService.getFile(projectId, filename)
        return ResponseEntity.ok()
            .contentLength(data.size.toLong())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            .body(ByteArrayResource(data))
    }

    override fun getFilesForProject(projectId: Long, pageable: Pageable): Page<OutputProjectFile> {
        return fileStorageService.getFilesForProject(projectId, pageable)
    }

    override fun setDescriptionToFile(
        projectId: Long,
        fileId: Long,
        projectFileDescription: InputProjectFileDescription
    ): OutputProjectFile {
        return fileStorageService.setDescription(projectId, fileId, projectFileDescription.description)
    }

}
