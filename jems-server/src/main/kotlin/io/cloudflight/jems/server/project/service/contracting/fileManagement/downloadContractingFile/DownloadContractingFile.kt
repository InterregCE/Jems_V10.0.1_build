package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadContractingFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectManagement
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadContractingFile(
    private val contractingFilePersistence: ProjectContractingFilePersistence,
) : DownloadContractingFileInteractor {

    @CanViewProjectManagement
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadContractingFileException::class)
    override fun download(projectId: Long, fileId: Long) =
        contractingFilePersistence.downloadFile(projectId = projectId, fileId = fileId)
            ?: throw FileNotFound()

}
