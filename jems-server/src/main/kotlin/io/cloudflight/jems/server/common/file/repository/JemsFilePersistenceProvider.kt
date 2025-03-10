package io.cloudflight.jems.server.common.file.repository

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.toFullModel
import io.cloudflight.jems.server.common.file.service.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream

@Repository
class JemsFilePersistenceProvider(
    private val projectFileMetadataRepository: JemsFileMetadataRepository,
    private val fileService: JemsProjectFileService,
    private val minioStorage: MinioStorage
) : JemsFilePersistence {

    @Transactional(readOnly = true)
    override fun existsFile(exactPath: String, fileName: String) =
        projectFileMetadataRepository.findOneByPathAndName(path = exactPath, name = fileName) != null

    @Transactional(readOnly = true)
    override fun fileIdIfExists(exactPath: String, fileName: String): Long? =
        projectFileMetadataRepository.findOneByPathAndName(path = exactPath, name = fileName)?.id

    @Transactional(readOnly = true)
    override fun existsFile(exactPath: String, fileId: Long): Boolean =
        projectFileMetadataRepository.findOneByPathAndId(exactPath, fileId) != null

    @Transactional(readOnly = true)
    override fun existsFile(partnerId: Long, pathPrefix: String, fileId: Long) =
        projectFileMetadataRepository.existsByPartnerIdAndPathPrefixAndId(
            partnerId = partnerId,
            pathPrefix,
            id = fileId
        )

    @Transactional(readOnly = true)
    override fun existsFile(type: JemsFileType, fileId: Long) =
        projectFileMetadataRepository.existsByTypeAndId(type, id = fileId)

    @Transactional(readOnly = true)
    override fun existsReportFile(projectId: Long, pathPrefix: String, fileId: Long) =
        projectFileMetadataRepository.existsByProjectIdAndPathPrefixAndId(
            projectId,
            pathPrefix,
            fileId
        )

    @Transactional(readOnly = true)
    override fun existsFileByProjectIdAndFileIdAndFileTypeIn(
        projectId: Long,
        fileId: Long,
        fileTypes: Set<JemsFileType>
    ): Boolean =
        projectFileMetadataRepository.existsByProjectIdAndIdAndTypeIn(
            projectId = projectId,
            fileId = fileId,
            fileTypes = fileTypes
        )

    @Transactional(readOnly = true)
    override fun existsFileByPartnerIdAndFileIdAndFileTypeIn(
        partnerId: Long,
        fileId: Long,
        fileTypes: Set<JemsFileType>
    ): Boolean =
        projectFileMetadataRepository.existsByPartnerIdAndIdAndTypeIn(
            partnerId = partnerId,
            fileId = fileId,
            fileTypes = fileTypes
        )

    @Transactional(readOnly = true)
    override fun listAttachments(
        pageable: Pageable,
        indexPrefix: String,
        filterSubtypes: Set<JemsFileType>,
        filterUserIds: Set<Long>,
    ): Page<JemsFile> =
        projectFileMetadataRepository.filterAttachment(
            pageable = pageable,
            indexPrefix = indexPrefix,
            filterSubtypes = filterSubtypes,
            filterUserIds = filterUserIds,
        ).toModel()

    @Transactional(readOnly = true)
    override fun getFile(partnerId: Long, pathPrefix: String, fileId: Long) =
        projectFileMetadataRepository.findByPartnerIdAndPathPrefixAndId(
            partnerId = partnerId,
            pathPrefix,
            id = fileId
        )?.toFullModel()

    @Transactional(readOnly = true)
    override fun getFile(projectId: Long, fileId: Long): JemsFile? =
        projectFileMetadataRepository.findByProjectIdAndId(projectId, fileId)?.toFullModel()

    @Transactional(readOnly = true)
    override fun downloadFile(partnerId: Long, fileId: Long) =
        projectFileMetadataRepository.findByPartnerIdAndId(partnerId = partnerId, fileId = fileId)?.download()

    @Transactional(readOnly = true)
    override fun downloadFile(type: JemsFileType, fileId: Long) =
        projectFileMetadataRepository.findByTypeAndId(type = type, fileId = fileId)?.download()

    @Transactional(readOnly = true)
    override fun downloadReportFile(projectId: Long, fileId: Long) =
        projectFileMetadataRepository.findByProjectIdAndId(projectId = projectId, fileId = fileId)?.download()

    @Transactional(readOnly = true)
    override fun downloadFileAsStream(type: JemsFileType, fileId: Long): Pair<String, InputStream>? =
        projectFileMetadataRepository.findByTypeAndId(type = type, fileId = fileId)?.downloadAsStream()

    @Transactional
    override fun deleteFile(partnerId: Long, fileId: Long) =
        projectFileMetadataRepository.findByPartnerIdAndId(partnerId = partnerId, fileId = fileId)
            .deleteIfPresent()

    @Transactional
    override fun deleteFile(type: JemsFileType, fileId: Long) =
        projectFileMetadataRepository.findByTypeAndId(type, fileId = fileId)
            .deleteIfPresent()

    @Transactional
    override fun deleteReportFile(projectId: Long, fileId: Long) {
        projectFileMetadataRepository.findByProjectIdAndId(projectId = projectId, fileId = fileId)
            .deleteIfPresent()
    }

    @Transactional
    override fun deleteFilesByPath(path: String) {
        val files = projectFileMetadataRepository.findAllByPath(path)
        fileService.deleteBatch(files)
    }


    @Transactional
    override fun setDescriptionToFile(fileId: Long, description: String) =
        fileService.setDescription(fileId, description)

    @Transactional(readOnly = true)
    override fun getFileType(fileId: Long, projectId: Long): JemsFileType? =
        projectFileMetadataRepository.findByProjectIdAndId(projectId, fileId)?.type


    @Transactional(readOnly = true)
    override fun getFileTypeByPartnerId(fileId: Long, partnerId: Long): JemsFileType =
        projectFileMetadataRepository.findByPartnerIdAndId(partnerId, fileId)?.type
            ?: throw ResourceNotFoundException("projectPartnerReportFileType")


    private fun JemsFileMetadataEntity.download() =
        Pair(name, minioStorage.getFile(minioBucket, filePath = minioLocation))

    private fun JemsFileMetadataEntity.downloadAsStream() =
        Pair(name, minioStorage.getFileAsStream(minioBucket, filePath = minioLocation))

    private fun JemsFileMetadataEntity?.deleteIfPresent() {
        if (this != null) {
            fileService.delete(this)
        }
    }
}
