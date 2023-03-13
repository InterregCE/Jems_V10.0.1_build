package io.cloudflight.jems.server.project.repository.report.project.annexes

import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.project.annexes.ProjectReportAnnexesFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectReportAnnexesFilePersistenceProvider(
    private val projectFileRepository: JemsProjectFileService
) : ProjectReportAnnexesFilePersistence {

    @Transactional
    override fun saveFile(projectFile: JemsFileCreate) =
        projectFileRepository.persistProjectFile(projectFile)
}
