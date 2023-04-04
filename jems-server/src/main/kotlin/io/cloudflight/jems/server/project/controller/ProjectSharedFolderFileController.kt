package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.ProjectSharedFolderFileApi
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.project.toProjectFile
import io.cloudflight.jems.server.project.service.sharedFolderFile.delete.DeleteFileFromSharedFolderInteractor
import io.cloudflight.jems.server.project.service.sharedFolderFile.description.SetDescriptionToSharedFolderFileInteractor
import io.cloudflight.jems.server.project.service.sharedFolderFile.download.DownloadSharedFolderFileInteractor
import io.cloudflight.jems.server.project.service.sharedFolderFile.list.ListSharedFolderFilesInteractor
import io.cloudflight.jems.server.project.service.sharedFolderFile.upload.UploadFileToSharedFolderInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectSharedFolderFileController(
    private val listSharedFolderFiles: ListSharedFolderFilesInteractor,
    private val uploadFileToSharedFolder: UploadFileToSharedFolderInteractor,
    private val setDescriptionToSharedFolderFile: SetDescriptionToSharedFolderFileInteractor,
    private val deleteFileFromSharedFolder: DeleteFileFromSharedFolderInteractor,
    private val downloadSharedFolderFile: DownloadSharedFolderFileInteractor,
) : ProjectSharedFolderFileApi {

    override fun listSharedFolderFiles(projectId: Long, pageable: Pageable): Page<JemsFileDTO> =
        listSharedFolderFiles.list(projectId, pageable).map { it.toDto() }

    override fun uploadFileToSharedFolder(projectId: Long, file: MultipartFile): JemsFileMetadataDTO =
        uploadFileToSharedFolder.upload(projectId, file.toProjectFile()).toDto()

    override fun setDescriptionToSharedFolderFile(projectId: Long, fileId: Long, description: String?) =
        setDescriptionToSharedFolderFile.set(projectId, fileId, description ?: "")

    override fun deleteSharedFolderFile(projectId: Long, fileId: Long) =
        deleteFileFromSharedFolder.delete(projectId, fileId)

    override fun downloadSharedFolderFile(projectId: Long, fileId: Long): ResponseEntity<ByteArrayResource> =
        with(downloadSharedFolderFile.download(projectId, fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }
}

