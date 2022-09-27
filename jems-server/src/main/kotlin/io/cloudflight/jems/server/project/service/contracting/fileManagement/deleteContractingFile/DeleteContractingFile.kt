package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractingFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectManagement
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteContractingFile(
    private val contractingFilePersistence: ProjectContractingFilePersistence,
) : DeleteContractingFileInteractor {

    @CanEditProjectManagement
    @Transactional
    @ExceptionWrapper(DeleteContractingFileException::class)
    override fun delete(projectId: Long, fileId: Long) {
        if (!contractingFilePersistence.existsFile(projectId = projectId, fileId = fileId))
            throw FileNotFound()

        contractingFilePersistence.deleteFile(projectId = projectId, fileId = fileId)
    }

}
