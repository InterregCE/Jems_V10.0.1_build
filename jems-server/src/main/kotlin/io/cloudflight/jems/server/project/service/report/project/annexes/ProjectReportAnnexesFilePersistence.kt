package io.cloudflight.jems.server.project.service.report.project.annexes

import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface ProjectReportAnnexesFilePersistence {

    fun saveFile(projectFile: JemsFileCreate): JemsFileMetadata
}
