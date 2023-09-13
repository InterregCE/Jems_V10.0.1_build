package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.repository.toSummaryModel
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class JemsProjectFileService(
    private val projectFileMetadataRepository: JemsFileMetadataRepository,
    private val minioStorage: MinioStorage,
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val auditPublisher: ApplicationEventPublisher,
) : JemsGenericFileService(projectFileMetadataRepository, minioStorage, userRepository) {

    companion object {
        private val allowedFileTypes = setOf(
            JemsFileType.PaymentAttachment,
            JemsFileType.PaymentAdvanceAttachment,

            // Project Report
            JemsFileType.ProjectReport,
            JemsFileType.ProjectResult,
            JemsFileType.ActivityProjectReport,
            JemsFileType.DeliverableProjectReport,
            JemsFileType.OutputProjectReport,

            // Project Report Verification
            JemsFileType.VerificationDocument,
            JemsFileType.VerificationCertificate,

            // Partner Report
            JemsFileType.PartnerReport,
            JemsFileType.Activity,
            JemsFileType.Deliverable,
            JemsFileType.Output,
            JemsFileType.Expenditure,
            JemsFileType.ProcurementAttachment,
            JemsFileType.ProcurementGdprAttachment,
            JemsFileType.Contribution,

            // Partner Control
            JemsFileType.ControlDocument,
            JemsFileType.ControlCertificate,
            JemsFileType.ControlReport,
            JemsFileType.Contract,
            JemsFileType.ContractDoc,
            JemsFileType.ContractPartnerDoc,
            JemsFileType.ContractInternal,

            // Shared Folder
            JemsFileType.SharedFolder,
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
        val fileMeta = super.persistFileAndPerformAction(file, additionalStep)

        val projectRelated = projectRepository.getById(file.projectId!!).toSummaryModel()
        auditPublisher.publishEvent(
            projectFileUploadSuccess(
                context = this, fileMeta = fileMeta.toSimple(),
                location = file.getDefaultMinioFullPath(), type = file.type, projectSummary = projectRelated
            )
        )

        return fileMeta
    }

    @Transactional
    fun setDescription(fileId: Long, description: String) {
        val file = projectFileMetadataRepository.findById(fileId).orElseThrow { ResourceNotFoundException("file") }
        validateType(file.type, allowedFileTypes)

        val oldDescription = file.description
        file.description = description

        val projectRelated = projectRepository.getById(file.projectId!!).toSummaryModel()

        auditPublisher.publishEvent(
            fileDescriptionChanged(
                context = this,
                fileMeta = file.toModel(),
                location = file.minioLocation,
                oldValue = oldDescription,
                newValue = description,
                projectSummary = projectRelated
            )
        )
    }

    @Transactional
    override fun delete(file: JemsFileMetadataEntity) {
        validateType(file.type, allowedFileTypes)

        val fileId = file.id
        val projectId = file.projectId!!

        super.delete(file)

        auditPublisher.publishEvent(
            fileDeleted(
                context = this, fileId = fileId,
                location = file.minioLocation, projectSummary = projectRepository.getById(projectId).toSummaryModel()
            )
        )
    }

}
