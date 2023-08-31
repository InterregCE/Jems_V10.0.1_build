package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class JemsSystemFileService(
    private val projectFileMetadataRepository: JemsFileMetadataRepository,
    private val minioStorage: MinioStorage,
    private val userRepository: UserRepository,
    private val auditPublisher: ApplicationEventPublisher,
) : JemsGenericFileService(projectFileMetadataRepository, minioStorage, userRepository) {

    companion object {
        private val allowedFileTypes = setOf(
            // Call-Specific translation file
            JemsFileType.CallTranslation,

            // Payment to EC
            JemsFileType.PaymentToEcAttachment
        )

        fun JemsFileCreate.getDefaultMinioFullPath() = "$path$name"
    }

    @Transactional
    fun persistFile(file: JemsFileCreate) =
        this.persistFileAndPerformAction(file) { /* do nothing */ }

    @Transactional
    override fun persistFileAndPerformAction(
        file: JemsFileCreate,
        additionalStep: (JemsFileMetadataEntity) -> Unit,
    ): JemsFile {
        validateType(file.type, allowedFileTypes)
        val fileMeta =  super.persistFileAndPerformAction(file, additionalStep)

        auditPublisher.publishEvent(
            systemFileUploadSuccess(
                context = this, fileMeta = fileMeta.toSimple(),
                location = file.getDefaultMinioFullPath(), type = file.type
            )
        )

        return fileMeta
    }

    @Transactional
    override fun delete(file: JemsFileMetadataEntity) {
        validateType(file.type, allowedFileTypes)

        val savedFile = file

        super.delete(file)

        auditPublisher.publishEvent(
            systemFileDeleted(
                context = this, fileMeta = savedFile.toModel(),
                location = savedFile.minioLocation, type = savedFile.type
            )
        )
    }

    @Transactional
    fun setDescription(fileId: Long, description: String) {
        val file = projectFileMetadataRepository.findById(fileId).orElseThrow { ResourceNotFoundException("file") }
        validateType(file.type, allowedFileTypes)

        val oldDescription = file.description
        file.description = description

        auditPublisher.publishEvent(
            systemFileDescriptionChanged(
                context = this,
                fileMeta = file.toModel(),
                location = file.minioLocation,
                oldValue = oldDescription,
                newValue = description
            )
        )
    }
}
