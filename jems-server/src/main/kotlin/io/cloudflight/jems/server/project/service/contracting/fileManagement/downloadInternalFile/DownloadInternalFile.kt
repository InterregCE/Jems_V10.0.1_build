package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadInternalFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectMonitoring
import io.cloudflight.jems.server.project.service.contracting.fileManagement.FileNotFound
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.contracting.fileManagement.validateInternalFile
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadInternalFile(
    private val contractingFilePersistence: ProjectContractingFilePersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
) : DownloadInternalFileInteractor {


    @CanViewProjectMonitoring
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadInternalFileException::class)
    override fun download(projectId: Long, fileId: Long): Pair<String, ByteArray> {
        validateInternalFile(reportFilePersistence.getFileType(fileId, projectId))
        return contractingFilePersistence.downloadFile(projectId = projectId, fileId = fileId)
            ?: throw FileNotFound()
    }

}
