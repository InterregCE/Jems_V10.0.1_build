package io.cloudflight.jems.server.project.service.sharedFolderFile.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanDeleteSharedFolderFile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteFileFromSharedFolder(
    private val filePersistence: JemsFilePersistence,
) : DeleteFileFromSharedFolderInteractor {

    @CanDeleteSharedFolderFile
    @Transactional
    @ExceptionWrapper(DeleteFileFromSharedFolderException::class)
    override fun delete(projectId: Long, fileId: Long) {
        if (!filePersistence.existsFile(type = JemsFileType.SharedFolder, fileId = fileId))
            throw FileNotFound()

        filePersistence.deleteFile(type = JemsFileType.SharedFolder, fileId = fileId)
    }
}
