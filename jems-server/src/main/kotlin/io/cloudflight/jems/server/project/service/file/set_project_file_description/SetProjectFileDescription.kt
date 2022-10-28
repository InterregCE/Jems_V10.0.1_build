package io.cloudflight.jems.server.project.service.file.set_project_file_description

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.minio.fileDescriptionChanged
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateFileDescriptionInCategory
import io.cloudflight.jems.server.project.repository.file.ProjectFilePersistenceProvider.Companion.getObjectPath
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetProjectFileDescription(
    private val filePersistence: ProjectFilePersistence,
    private val projectPersistence: ProjectPersistence,
    private val generalValidatorService: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher
) : SetProjectFileDescriptionInteractor {

    @CanUpdateFileDescriptionInCategory
    @Transactional
    @ExceptionWrapper(SetProjectFileDescriptionExceptions::class)
    override fun setDescription(
        projectId: Long, fileId: Long, description: String?
    ): ProjectFileMetadata {
        throwIfDescriptionIsNotValid(description)
        projectPersistence.throwIfNotExists(projectId)
        val file = filePersistence.getFileMetadata(fileId)
        val oldDescription = file.description ?: ""
        return filePersistence.setFileDescription(fileId, description).also {
            val location = getObjectPath(projectId, file.id, file.name)
            auditPublisher.publishEvent(
                fileDescriptionChanged(context = this, fileMeta = ProjectReportFileMetadata(file.id, file.name, file.uploadedAt),
                    location = location, oldDescription, description ?: "", projectPersistence.getProjectSummary(projectId))
            )
        }
    }

    private fun throwIfDescriptionIsNotValid(description: String?) {
        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.maxLength(description, 250, "description")
        )
    }
}
