package io.cloudflight.jems.server.common.minio

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.repository.report.file.toEntity
import io.cloudflight.jems.server.project.repository.report.toModel
import io.cloudflight.jems.server.project.repository.toSummaryModel
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
        const val BUCKET = "project-report"
    }

    @Transactional
    fun persistProjectFile(file: ProjectReportFileCreate, locationForMinio: String) =
        persistProjectFileAndPerformAction(file, locationForMinio) { /* do nothing */ }

    @Transactional
    fun persistProjectFileAndPerformAction(
        file: ProjectReportFileCreate,
        locationForMinio: String,
        additionalStep: (ReportProjectFileEntity) -> Unit,
    ): ProjectReportFileMetadata {
        minioStorage.saveFile(
            bucket = BUCKET,
            filePath = locationForMinio,
            size = file.size,
            stream = file.content,
            overwriteIfExists = true,
        )
        val fileMeta = reportFileRepository.save(
            file.toEntity(
                bucketForMinio = BUCKET,
                locationForMinio = locationForMinio,
                userResolver = { userRepository.getById(it) },
                uploaded = ZonedDateTime.now(),
            ).also { additionalStep.invoke(it) }
        ).toModel()

        val projectRelated = projectRepository.getById(file.projectId!!).toSummaryModel()
        auditPublisher.publishEvent(projectFileUploadSuccess(context = this, fileMeta = fileMeta,
            location = locationForMinio, type = file.type, projectSummary = projectRelated))

        return fileMeta
    }

    @Transactional
    fun setDescription(fileId: Long, description: String) {
        val file = reportFileRepository.findById(fileId).orElseThrow { ResourceNotFoundException("file") }
        val oldDescription = file.description
        file.description = description

        val projectRelated = projectRepository.getById(file.projectId!!).toSummaryModel()

        auditPublisher.publishEvent(fileDescriptionChanged(context = this, fileMeta = file.toModel(),
            location = file.minioLocation, oldValue = oldDescription, newValue = description, projectSummary = projectRelated))
    }

}
