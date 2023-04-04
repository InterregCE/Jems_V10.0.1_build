package io.cloudflight.jems.server.project.service.report.project.annexes.download

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadProjectReportAnnexesFile(
    private val filePersistence: JemsFilePersistence
) : DownloadProjectReportAnnexesFileInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadProjectReportAnnexesFileException::class)
    override fun download(projectId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray> {

        val projectReportPrefix = JemsFileType.ProjectReport.generatePath(projectId, reportId)

        return filePersistence.existsReportFile(projectId, projectReportPrefix, fileId)
            .let { exists -> if (exists) filePersistence.downloadReportFile(projectId, fileId) else null }
            ?: throw FileNotFound()
    }
}
