package io.cloudflight.jems.server.project.service.report.project.annexes

import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata

interface ProjectReportAnnexesFilePersistence {

    fun saveFile(projectFile: JemsFileCreate): JemsFileMetadata
}
