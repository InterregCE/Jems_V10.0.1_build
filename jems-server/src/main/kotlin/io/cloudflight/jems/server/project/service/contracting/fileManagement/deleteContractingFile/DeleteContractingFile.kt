package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractingFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.ProjectContractInfoAuthorization
import io.cloudflight.jems.server.project.authorization.ProjectMonitoringAuthorization
import io.cloudflight.jems.server.project.service.contracting.fileManagement.CONTRACT_ALLOWED_FILE_TYPES
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteContractingFile(
    private val contractingFilePersistence: ProjectContractingFilePersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val contractInfoAuth: ProjectContractInfoAuthorization,
    private val projectMonitoringAuthorization: ProjectMonitoringAuthorization
) : DeleteContractingFileInteractor {

    @Transactional
    @ExceptionWrapper(DeleteContractingFileException::class)
    override fun delete(projectId: Long, fileId: Long) {
        if (!contractingFilePersistence.existsFile(projectId = projectId, fileId = fileId))
            throw FileNotFound()

        reportFilePersistence.getFileType(fileId, projectId)?.let { fileType ->
            if ((contractInfoAuth.canEditContractInfo(projectId) &&
                    fileType in CONTRACT_ALLOWED_FILE_TYPES.keys) ||
                (projectMonitoringAuthorization.canEditProjectMonitoring(projectId) &&
                    fileType == ProjectPartnerReportFileType.ContractInternal)
            ) {
                contractingFilePersistence.deleteFile(projectId = projectId, fileId = fileId)
            }else {
                throw FileNotFound()
            }
        }

    }

}
