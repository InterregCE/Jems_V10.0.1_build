package io.cloudflight.jems.server.project.service.report.project.annexes.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectReportAnnexesFile(
    private val filePersistence: JemsFilePersistence
) : DeleteProjectReportAnnexesFileInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(DeleteProjectReportAnnexesFileException::class)
    override fun delete(projectId: Long, reportId: Long, fileId: Long) {
        val projectReportPrefix = JemsFileType.ProjectReport.generatePath(projectId, reportId)
        if (!filePersistence.existsReportFile(projectId, projectReportPrefix, fileId))
            throw FileNotFound()

        filePersistence.deleteReportFile(projectId, fileId)
    }
}
