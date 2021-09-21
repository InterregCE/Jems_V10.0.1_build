package io.cloudflight.jems.server.project.service.file.set_project_file_description

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateFileDescriptionInCategory
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.project.service.projectFileDescriptionChanged
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
        throwIdDescriptionIsNotValid(description)
        projectPersistence.throwIfNotExists(projectId)
        val oldDescription = filePersistence.getFileMetadata(fileId).description ?: ""
        return filePersistence.setFileDescription(fileId, description).also {
            auditPublisher.publishEvent(
                projectFileDescriptionChanged(this, it, oldDescription)
            )
        }
    }

    fun throwIdDescriptionIsNotValid(description: String?) {
        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.maxLength(description, 100, "description")
        )
    }
}
