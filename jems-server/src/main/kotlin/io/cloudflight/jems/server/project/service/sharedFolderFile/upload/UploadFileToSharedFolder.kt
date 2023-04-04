package io.cloudflight.jems.server.project.service.sharedFolderFile.upload

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanEditSharedFolder
import io.cloudflight.jems.server.project.repository.file.ProjectFileTypeNotSupported
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToSharedFolder(
    private val projectPersistence: ProjectPersistence,
    private val filePersistence: JemsFilePersistence,
    private val projectFileService: JemsProjectFileService,
    private val securityService: SecurityService,
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

            return projectFileService.persistProjectFile(
                file.getFileMetadata(
                    projectId = projectId,
                    partnerId = null,
                    location = location,
                    type = this,
                    userId = securityService.getUserIdOrThrow(),
                )
            )

        }

    }
}
