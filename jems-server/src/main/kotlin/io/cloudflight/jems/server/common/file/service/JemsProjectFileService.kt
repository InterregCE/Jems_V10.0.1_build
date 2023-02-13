package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.minio.*
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.repository.toSummaryModel
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class JemsProjectFileService(
    private val projectFileMetadataRepository: JemsFileMetadataRepository,
    private val minioStorage: MinioStorage,
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val auditPublisher: ApplicationEventPublisher,
) {
    companion object {
        private val allowedFileTypes = setOf(
            JemsFileType.PaymentAttachment,
            JemsFileType.PaymentAdvanceAttachment,

            JemsFileType.PartnerReport,
            JemsFileType.Activity,
            JemsFileType.Deliverable,
            JemsFileType.Output,
            JemsFileType.Expenditure,
            JemsFileType.ProcurementAttachment,
            JemsFileType.Contribution,

            JemsFileType.ControlDocument,
            JemsFileType.ControlCertificate,
            JemsFileType.ControlReport,
            JemsFileType.Contract,
            JemsFileType.ContractDoc,
            JemsFileType.ContractPartnerDoc,
            JemsFileType.ContractInternal,
        )

        fun JemsFileCreate.getDefaultMinioFullPath() = "$path$name"

    }

    @Transactional
    fun persistProjectFile(file: JemsFileCreate) =
        persistProjectFileAndPerformAction(file) { /* do nothing */ }

    @Transactional
    fun persistProjectFileAndPerformAction(
        file: JemsFileCreate,
        additionalStep: (JemsFileMetadataEntity) -> Unit,
    ): JemsFileMetadata {
        validateType(file.type)

        val bucket = file.type.getBucket()
        val locationForMinio = file.getDefaultMinioFullPath()

        minioStorage.saveFile(
            bucket = bucket,
            filePath = locationForMinio,
            size = file.size,
            stream = file.content,
            overwriteIfExists = true,
        )
        val fileMeta = projectFileMetadataRepository.save(
            file.toEntity(
                bucketForMinio = bucket,
                locationForMinio = locationForMinio,
                userResolver = { userRepository.getById(it) },
                uploaded = ZonedDateTime.now(),
            )
        ).also { additionalStep.invoke(it) }.toModel()

        val projectRelated = projectRepository.getById(file.projectId!!).toSummaryModel()
        auditPublisher.publishEvent(
            projectFileUploadSuccess(context = this, fileMeta = fileMeta,
            location = locationForMinio, type = file.type, projectSummary = projectRelated)
        )

        return fileMeta
    }

    @Transactional
    fun setDescription(fileId: Long, description: String) {
        val file = projectFileMetadataRepository.findById(fileId).orElseThrow { ResourceNotFoundException("file") }
        validateType(file.type)

        val oldDescription = file.description
        file.description = description

        val projectRelated = projectRepository.getById(file.projectId!!).toSummaryModel()

        auditPublisher.publishEvent(
            fileDescriptionChanged(context = this, fileMeta = file.toModel(),
            location = file.minioLocation, oldValue = oldDescription, newValue = description, projectSummary = projectRelated)
        )
    }

    @Transactional
    fun delete(file: JemsFileMetadataEntity) {
        validateType(file.type)

        val fileId = file.id
        val projectId = file.projectId!!

        minioStorage.deleteFile(bucket = file.minioBucket, filePath = file.minioLocation)
        projectFileMetadataRepository.delete(file)

        auditPublisher.publishEvent(
            fileDeleted(context = this, fileId = fileId,
            location = file.minioLocation, projectSummary = projectRepository.getById(projectId).toSummaryModel())
        )
    }

    private fun validateType(type: JemsFileType) {
        if (type !in allowedFileTypes) {
            throw WrongFileTypeException(type)
        }
    }

}

class WrongFileTypeException(type: JemsFileType): ApplicationUnprocessableException(
    code = "GENERIC-FILE-EXCEPTION",
    i18nMessage = I18nMessage("not.allowed.file.type.in.generic.project.file.repository"),
    message = type.name,
)
