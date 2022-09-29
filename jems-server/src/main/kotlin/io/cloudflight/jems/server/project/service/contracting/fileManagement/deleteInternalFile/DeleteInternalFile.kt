package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteInternalFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectMonitoring
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.contracting.fileManagement.validateInternalFile
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteInternalFile(
    private val contractingFilePersistence: ProjectContractingFilePersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
) : DeleteInternalFileInteractor {

    @CanEditProjectMonitoring
    @Transactional
    @ExceptionWrapper(DeleteContractingInternalFileException::class)
    override fun delete(projectId: Long, fileId: Long) {
        validateInternalFile(reportFilePersistence.getFileType(fileId, projectId))
        contractingFilePersistence.deleteFile(projectId = projectId, fileId = fileId)
    }


}



