package io.cloudflight.jems.server.project.service.file.delete_project_file

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanDeleteFileInCategory
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.projectFileDeleteSucceed
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectFile(
    private val filePersistence: ProjectFilePersistence,
    private val projectPersistence: ProjectPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : DeleteProjectFileInteractor {

    @CanDeleteFileInCategory
    @Transactional
    @ExceptionWrapper(DeleteProjectFileException::class)
    override fun delete(projectId: Long, fileId: Long) {
        projectPersistence.throwIfNotExists(projectId)
        filePersistence.getFileMetadata(fileId).also { fileMetadata ->
            throwIfFileCannotBeRemoved(
                fileMetadata, filePersistence.getFileCategoryTypeSet(fileId), projectPersistence.getProject(projectId)
            )
            filePersistence.deleteFile(projectId, fileId, fileMetadata.name)
            auditPublisher.publishEvent(projectFileDeleteSucceed(this, fileMetadata))
        }
    }

    private fun throwIfFileCannotBeRemoved(
        fileMetadata: ProjectFileMetadata, fileCategories: Set<ProjectFileCategoryType>, project: ProjectFull
    ) {
        if (fileCategories.isNotEmpty() && fileCategories.any {
                it == ProjectFileCategoryType.APPLICATION || it == ProjectFileCategoryType.PARTNER || it == ProjectFileCategoryType.INVESTMENT
            }
            && fileMetadata.uploadedAt.isBefore(project.projectStatus.updated)
        ) throw DeletingOldFileFromApplicationCategoryIsNotAllowedException()
    }
}
