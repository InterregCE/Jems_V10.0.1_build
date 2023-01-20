package io.cloudflight.jems.server.project.repository.contracting.management.file

import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectContractingFilePersistenceProvider(
    private val projectFileMetadataRepository: JemsFileMetadataRepository,
    private val minioStorage: MinioStorage,
    private val fileRepository: JemsProjectFileService,
) : ProjectContractingFilePersistence {

    @Transactional(readOnly = true)
    override fun downloadFile(projectId: Long, fileId: Long) =
        projectFileMetadataRepository.findByProjectIdAndId(projectId = projectId, fileId = fileId)?.let { file ->
            Pair(file.name, minioStorage.getFile(file.minioBucket, filePath = file.minioLocation))
        }

    @Transactional(readOnly = true)
    override fun downloadFileByPartnerId(partnerId: Long, fileId: Long) =
        projectFileMetadataRepository.findByPartnerIdAndId(partnerId = partnerId, fileId = fileId)?.let { file ->
            Pair(file.name, minioStorage.getFile(file.minioBucket, filePath = file.minioLocation))
        }

    @Transactional(readOnly = true)
    override fun existsFile(projectId: Long, fileId: Long) =
        projectFileMetadataRepository.existsByProjectIdAndId(projectId = projectId, fileId)

    @Transactional
    override fun deleteFile(projectId: Long, fileId: Long) =
        projectFileMetadataRepository.findByProjectIdAndId(projectId = projectId, fileId = fileId)
            .deleteIfPresent()

    @Transactional
    override fun deleteFileByPartnerId(partnerId: Long, fileId: Long) {
        projectFileMetadataRepository.findByPartnerIdAndId(partnerId = partnerId, fileId = fileId)
            .deleteIfPresent()
    }

    private fun JemsFileMetadataEntity?.deleteIfPresent() {
        if (this != null) {
            fileRepository.delete(this)
        }
    }

}
