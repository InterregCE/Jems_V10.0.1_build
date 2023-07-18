package io.cloudflight.jems.server.project.service.sharedFolderFile.delete

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.authorization.CanDeleteSharedFolderFile
import io.cloudflight.jems.server.project.service.ProjectPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteFileFromSharedFolder(
    private val filePersistence: JemsFilePersistence,
    private val projectPersistence: ProjectPersistence,
    private val securityService: SecurityService,
    private val auditPublisher: ApplicationEventPublisher,
) : DeleteFileFromSharedFolderInteractor {

    @CanDeleteSharedFolderFile
    @Transactional
    @ExceptionWrapper(DeleteFileFromSharedFolderException::class)
    override fun delete(projectId: Long, fileId: Long) {
        val file = filePersistence.getFile(fileId, projectId) ?: throw FileNotFound()

        filePersistence.deleteFile(type = JemsFileType.SharedFolder, fileId = fileId).also {
            auditPublisher.publishEvent(
                ProjectFileChangeEvent(
                    action = FileChangeAction.Delete,
                    projectSummary = projectPersistence.getProjectSummary(projectId),
                    file = file!!,
                    overrideAuthorEmail = securityService.currentUser?.user?.email
                )
            )
        }
    }
}
