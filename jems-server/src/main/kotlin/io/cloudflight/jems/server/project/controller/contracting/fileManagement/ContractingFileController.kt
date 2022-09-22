package io.cloudflight.jems.server.project.controller.contracting.fileManagement

import io.cloudflight.jems.api.project.contracting.ContractingFileApi
import io.cloudflight.jems.api.project.dto.contracting.file.ProjectContractingFileSearchRequestDTO
import io.cloudflight.jems.server.project.controller.report.toDto
import io.cloudflight.jems.server.project.controller.report.toProjectFile
import io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractingFile.DeleteContractingFileInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadContractingFile.DownloadContractingFileInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles.ListContractingFilesInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.setContractFileDescription.SetContractFileDescriptionInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.setInternalFileDescription.SetInternalFileDescriptionInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.uploadFileToContracting.UploadFileToContractingInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ContractingFileController(
    private val uploadToContracting: UploadFileToContractingInteractor,
    private val listContractingFiles: ListContractingFilesInteractor,
    private val downloadContractingFile: DownloadContractingFileInteractor,
    private val deleteContractingFile: DeleteContractingFileInteractor,
    private val setContractFileDescriptionInteractor: SetContractFileDescriptionInteractor,
    private val setInternalFileDescriptionInteractor: SetInternalFileDescriptionInteractor
): ContractingFileApi {

    override fun uploadContractFile(projectId: Long, file: MultipartFile) =
        uploadToContracting.uploadContract(projectId, file.toProjectFile()).toDto()

    override fun uploadContractDocumentFile(projectId: Long, file: MultipartFile) =
        uploadToContracting.uploadContractDocument(projectId, file.toProjectFile()).toDto()

    override fun uploadContractFileForPartner(projectId: Long, partnerId: Long, file: MultipartFile) =
        uploadToContracting.uploadContractPartnerFile(projectId, partnerId, file.toProjectFile()).toDto()

    override fun uploadContractInternalFile(projectId: Long, file: MultipartFile) =
        uploadToContracting.uploadContractInternalFile(projectId, file.toProjectFile()).toDto()

    override fun listFiles(
        projectId: Long,
        partnerId: Long?,
        pageable: Pageable,
        searchRequest: ProjectContractingFileSearchRequestDTO
    ) = listContractingFiles.list(
        projectId = projectId,
        partnerId = if (partnerId == 0L) null else partnerId,
        pageable = pageable,
        searchRequest = searchRequest.toModel(),
    ).map { it.toDto() }

    override fun downloadFile(projectId: Long, fileId: Long): ResponseEntity<ByteArrayResource> =
        with(downloadContractingFile.download(projectId, fileId = fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }

    override fun deleteFile(projectId: Long, fileId: Long) =
        deleteContractingFile.delete(projectId, fileId = fileId)

    override fun updateContractFileDescription(projectId: Long, fileId: Long, description: String?) {
        setContractFileDescriptionInteractor.setContractFileDescription(
            projectId = projectId,
            fileId = fileId,
            description = description ?: ""
        )
    }

    override fun updateInternalFileDescription(projectId: Long, fileId: Long, description: String?) {
        setInternalFileDescriptionInteractor.setInternalFileDescription(
            projectId = projectId,
            fileId = fileId,
            description = description ?: ""
        )
    }
}
