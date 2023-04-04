package io.cloudflight.jems.server.project.service.sharedFolderFile.delete

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanEditSharedFolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteFileFromSharedFolder(
    private val filePersistence: JemsFilePersistence,
    private val securityService: SecurityService,
) : DeleteFileFromSharedFolderInteractor {

    @CanEditSharedFolder
    @Transactional
    @ExceptionWrapper(DeleteFileFromSharedFolderException::class)
    override fun delete(projectId: Long, fileId: Long) {

        val author = filePersistence.getProjectFileAuthor(projectId = projectId, fileId = fileId)
            ?: throw FileNotFound()

        if (author.id != securityService.getUserIdOrThrow()) {
            throw UserIsNotOwnerOfFile()
        }

        filePersistence.deleteFile(type = JemsFileType.SharedFolder, fileId = fileId)
    }
}
