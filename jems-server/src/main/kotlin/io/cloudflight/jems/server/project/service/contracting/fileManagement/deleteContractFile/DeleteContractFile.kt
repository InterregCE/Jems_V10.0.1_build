package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditContractInfo
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.contracting.fileManagement.validateContractFile
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteContractFile(
    private val contractingFilePersistence: ProjectContractingFilePersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
) : DeleteContractFileInteractor {

    @CanEditContractInfo
    @Transactional
    @ExceptionWrapper(DeleteContractFileException::class)
    override fun delete(projectId: Long, fileId: Long) {
        validateContractFile(reportFilePersistence.getFileType(fileId, projectId))
        contractingFilePersistence.deleteFile(projectId = projectId, fileId = fileId)
    }

}
