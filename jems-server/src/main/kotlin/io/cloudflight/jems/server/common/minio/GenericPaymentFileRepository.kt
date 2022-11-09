package io.cloudflight.jems.server.common.minio

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.repository.report.file.toEntity
import io.cloudflight.jems.server.project.repository.report.toModel
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class GenericPaymentFileRepository(
    private val reportFileRepository: ProjectReportFileRepository,
    private val minioStorage: MinioStorage,
    private val userRepository: UserRepository,
) {

    companion object {
        const val BUCKET = "payment"

        private val allowedFileTypes = setOf(
            ProjectPartnerReportFileType.PaymentAttachment,
            ProjectPartnerReportFileType.PaymentAdvancedAttachment,
        )
    }

    @Transactional
    fun persistFile(file: ProjectReportFileCreate, locationForMinio: String) =
        persistPaymentFileAndPerformAction(file, locationForMinio) { /* do nothing */ }

    // can be made public if needed
    private fun persistPaymentFileAndPerformAction(
        file: ProjectReportFileCreate,
        locationForMinio: String,
        additionalStep: (ReportProjectFileEntity) -> Unit,
    ): ProjectReportFileMetadata {
        validateType(file.type)

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
        ).also { additionalStep.invoke(it) }.toModel()
    }

    @Transactional
    fun setDescription(type: ProjectPartnerReportFileType, fileId: Long, description: String) {
        validateType(type)

        val file = reportFileRepository.findByTypeAndId(type, fileId) ?: throw ResourceNotFoundException("file")
        file.description = description
    }

    @Transactional
    fun delete(type: ProjectPartnerReportFileType, fileId: Long) {
        validateType(type)

        val file = reportFileRepository.findByTypeAndId(type, fileId) ?: throw ResourceNotFoundException("file")

        minioStorage.deleteFile(bucket = file.minioBucket, filePath = file.minioLocation)
        reportFileRepository.delete(file)
    }

    private fun validateType(type: ProjectPartnerReportFileType) {
        if (type !in allowedFileTypes) {
            throw WrongFileTypeException()
        }
    }

}

class WrongFileTypeException: ApplicationUnprocessableException(
    code = "GENERIC-FILE-EXCEPTION",
    i18nMessage = I18nMessage("not.allowed.file.type.for.deletion"),
)
