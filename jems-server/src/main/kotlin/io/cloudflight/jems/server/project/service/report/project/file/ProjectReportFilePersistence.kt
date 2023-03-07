package io.cloudflight.jems.server.project.service.report.project.file

import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface ProjectReportFilePersistence {

    fun updateProjectResultAttachment(reportId: Long, resultNumber: Int, file: JemsFileCreate): JemsFileMetadata

}
