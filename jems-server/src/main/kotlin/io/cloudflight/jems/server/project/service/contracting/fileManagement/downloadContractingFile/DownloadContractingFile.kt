package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadContractingFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.ProjectContractInfoAuthorization
import io.cloudflight.jems.server.project.authorization.ProjectMonitoringAuthorization
import io.cloudflight.jems.server.project.service.contracting.fileManagement.CONTRACT_ALLOWED_FILE_TYPES
import io.cloudflight.jems.server.project.service.contracting.fileManagement.MONITORING_ALLOWED_FILE_TYPES
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadContractingFile(
    private val contractingFilePersistence: ProjectContractingFilePersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val contractInfoAuth: ProjectContractInfoAuthorization,
    private val projectMonitoringAuthorization: ProjectMonitoringAuthorization
) : DownloadContractingFileInteractor {


    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadContractingFileException::class)
    override fun download(projectId: Long, fileId: Long): Pair<String, ByteArray> {

        reportFilePersistence.getFileType(fileId, projectId)?.let { fileType ->
            if ((contractInfoAuth.canViewContractInfo(projectId) &&
                    fileType in CONTRACT_ALLOWED_FILE_TYPES.keys) ||
                (projectMonitoringAuthorization.canViewProjectMonitoring(projectId) &&
                    fileType in MONITORING_ALLOWED_FILE_TYPES.keys)
            ) {
                return contractingFilePersistence.downloadFile(projectId = projectId, fileId = fileId)
                    ?: throw FileNotFound()
            }
        }
        throw FileNotFound()
    }
}
