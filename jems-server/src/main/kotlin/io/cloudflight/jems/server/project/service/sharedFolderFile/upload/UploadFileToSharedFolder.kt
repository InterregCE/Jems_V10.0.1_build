package io.cloudflight.jems.server.project.service.sharedFolderFile.upload

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.authorization.CanEditSharedFolder
import io.cloudflight.jems.server.project.repository.file.ProjectFileTypeNotSupported
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToSharedFolder(
    private val projectPersistence: ProjectPersistence,
    private val filePersistence: JemsFilePersistence,
    private val projectFileService: JemsProjectFileService,
    private val securityService: SecurityService,
    private val eventPublisher: ApplicationEventPublisher
) : UploadFileToSharedFolderInteractor {

    @CanEditSharedFolder
    @Transactional
    @ExceptionWrapper(UploadFileToSharedFolderException::class)
    override fun upload(projectId: Long, file: ProjectFile): JemsFileMetadata {

        if (isFileTypeInvalid(file)) {
            throw ProjectFileTypeNotSupported()
        }

        projectPersistence.throwIfNotExists(projectId = projectId)

        with(JemsFileType.SharedFolder) {
            val location = generatePath(projectId)

            if (filePersistence.existsFile(exactPath = location, fileName = file.name)) {
                throw FileAlreadyExists()
            }
            val fileMetadata = file.getFileMetadata(
                projectId = projectId,
                partnerId = null,
                location = location,
                type = this,
                userId = securityService.getUserIdOrThrow(),
            )

            return projectFileService.persistFile(
                fileMetadata
            ).also {
                eventPublisher.publishEvent(
                    ProjectFileChangeEvent(
                        action = FileChangeAction.Upload,
                        projectSummary = projectPersistence.getProjectSummary(projectId),
                        file = it,
                    )
                )
            }.toSimple()
        }

    }
}
