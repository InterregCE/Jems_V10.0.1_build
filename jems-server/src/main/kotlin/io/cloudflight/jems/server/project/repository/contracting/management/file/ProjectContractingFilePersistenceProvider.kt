package io.cloudflight.jems.server.project.repository.contracting.management.file

import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFilePersistenceProvider.Companion.BUCKET
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.repository.report.file.getMinioFullPath
import io.cloudflight.jems.server.project.repository.report.file.toEntity
import io.cloudflight.jems.server.project.repository.report.toModel
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class ProjectContractingFilePersistenceProvider(
    private val reportFileRepository: ProjectReportFileRepository,
    private val minioStorage: MinioStorage,
    private val userRepository: UserRepository,
) : ProjectContractingFilePersistence {

    @Transactional
    override fun uploadFile(file: ProjectReportFileCreate) =
        persistFileAndMetadata(file, locationForMinio = file.getMinioFullPath()).toModel()

    @Transactional(readOnly = true)
    override fun downloadFile(projectId: Long, fileId: Long) =
        reportFileRepository.findByProjectIdAndId(projectId = projectId, fileId = fileId)?.let { file ->
            Pair(file.name, minioStorage.getFile(file.minioBucket, filePath = file.minioLocation))
        }

    @Transactional(readOnly = true)
    override fun downloadFileByPartnerId(partnerId: Long, fileId: Long) =
        reportFileRepository.findByPartnerIdAndId(partnerId = partnerId, fileId = fileId)?.let { file ->
            Pair(file.name, minioStorage.getFile(file.minioBucket, filePath = file.minioLocation))
        }

    @Transactional(readOnly = true)
    override fun existsFile(projectId: Long, fileId: Long) =
        reportFileRepository.existsByProjectIdAndId(projectId = projectId, fileId)

    @Transactional
    override fun deleteFile(projectId: Long, fileId: Long) =
        reportFileRepository.findByProjectIdAndId(projectId = projectId, fileId = fileId)
            .deleteIfPresent()

    @Transactional
    override fun deleteFileByPartnerId(partnerId: Long, fileId: Long) {
        reportFileRepository.findByPartnerIdAndId(partnerId = partnerId, fileId = fileId)
            .deleteIfPresent()
    }

    private fun persistFileAndMetadata(file: ProjectReportFileCreate, locationForMinio: String): ReportProjectFileEntity {
        minioStorage.saveFile(
            bucket = BUCKET,
            filePath = locationForMinio,
            size = file.size,
            stream = file.content,
            overwriteIfExists = true,
        )
        return reportFileRepository.save(
            file.toEntity(
                bucketForMinio = BUCKET,
                locationForMinio = locationForMinio,
                userResolver = { userRepository.getById(it) },
                uploaded = ZonedDateTime.now(),
            )
        )
    }

    private fun ReportProjectFileEntity?.deleteIfPresent() {
        if (this != null) {
            minioStorage.deleteFile(bucket = minioBucket, filePath = minioLocation)
            reportFileRepository.delete(this)
        }
    }

}
