package io.cloudflight.jems.server.common.minio

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.repository.report.file.getMinioFullPath
import io.cloudflight.jems.server.project.repository.report.file.toEntity
import io.cloudflight.jems.server.project.repository.report.toModel
import io.cloudflight.jems.server.project.repository.toSummaryModel
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class GenericProjectFileRepository(
    private val reportFileRepository: ProjectReportFileRepository,
    private val minioStorage: MinioStorage,
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val auditPublisher: ApplicationEventPublisher,
) {
    companion object {
        private val allowedFileTypes = setOf(
            ProjectPartnerReportFileType.PaymentAttachment,
            ProjectPartnerReportFileType.PaymentAdvanceAttachment,

            ProjectPartnerReportFileType.PartnerReport,
            ProjectPartnerReportFileType.Activity,
            ProjectPartnerReportFileType.Deliverable,
            ProjectPartnerReportFileType.Output,
            ProjectPartnerReportFileType.Expenditure,
            ProjectPartnerReportFileType.ProcurementAttachment,
            ProjectPartnerReportFileType.Contribution,

            ProjectPartnerReportFileType.ControlDocument,
            ProjectPartnerReportFileType.Contract,
            ProjectPartnerReportFileType.ContractDoc,
            ProjectPartnerReportFileType.ContractPartnerDoc,
            ProjectPartnerReportFileType.ContractInternal,
        )
    }

    @Transactional
    fun persistProjectFile(file: ProjectReportFileCreate) =
        persistProjectFileAndPerformAction(file) { /* do nothing */ }

    @Transactional
    fun persistProjectFileAndPerformAction(
        file: ProjectReportFileCreate,
        additionalStep: (ReportProjectFileEntity) -> Unit,
    ): ProjectReportFileMetadata {
        validateType(file.type)

        val bucket = file.type.getBucket()
        val locationForMinio = file.getMinioFullPath()

        minioStorage.saveFile(
            bucket = bucket,
            filePath = locationForMinio,
            size = file.size,
            stream = file.content,
            overwriteIfExists = true,
        )
        val fileMeta = reportFileRepository.save(
            file.toEntity(
                bucketForMinio = bucket,
                locationForMinio = locationForMinio,
                userResolver = { userRepository.getById(it) },
                uploaded = ZonedDateTime.now(),
            )
        ).also { additionalStep.invoke(it) }.toModel()

        val projectRelated = projectRepository.getById(file.projectId!!).toSummaryModel()
        auditPublisher.publishEvent(projectFileUploadSuccess(context = this, fileMeta = fileMeta,
            location = locationForMinio, type = file.type, projectSummary = projectRelated))

        return fileMeta
    }

    @Transactional
    fun setDescription(fileId: Long, description: String) {
        val file = reportFileRepository.findById(fileId).orElseThrow { ResourceNotFoundException("file") }
        validateType(file.type)

        val oldDescription = file.description
        file.description = description

        val projectRelated = projectRepository.getById(file.projectId!!).toSummaryModel()

        auditPublisher.publishEvent(fileDescriptionChanged(context = this, fileMeta = file.toModel(),
            location = file.minioLocation, oldValue = oldDescription, newValue = description, projectSummary = projectRelated))
    }

    @Transactional
    fun delete(file: ReportProjectFileEntity) {
        validateType(file.type)

        val fileId = file.id
        val projectId = file.projectId!!

        minioStorage.deleteFile(bucket = file.minioBucket, filePath = file.minioLocation)
        reportFileRepository.delete(file)

        auditPublisher.publishEvent(fileDeleted(context = this, fileId = fileId,
            location = file.minioLocation, projectSummary = projectRepository.getById(projectId).toSummaryModel())
        )
    }

    private fun validateType(type: ProjectPartnerReportFileType) {
        if (type !in allowedFileTypes) {
            throw WrongFileTypeException(type)
        }
    }

}

class WrongFileTypeException(type: ProjectPartnerReportFileType): ApplicationUnprocessableException(
    code = "GENERIC-FILE-EXCEPTION",
    i18nMessage = I18nMessage("not.allowed.file.type.in.generic.project.file.repository"),
    message = type.name,
)
