package io.cloudflight.jems.server.project.repository.report.project.file

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.ProjectReportProjectResultRepository
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportFilePersistenceProvider(
    private val fileService: JemsProjectFileService,
    private val projectResultRepository: ProjectReportProjectResultRepository
) : ProjectReportFilePersistence {

    @Transactional
    override fun updateProjectResultAttachment(reportId: Long, resultNumber: Int, file: JemsFileCreate): JemsFileMetadata {
        val projectResult = projectResultRepository.findByProjectReportIdAndResultNumber(
            reportId = reportId, resultNumber = resultNumber
        )

        projectResult.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { projectResult.attachment = it }
    }

    private fun persistFileAndUpdateLink(file: JemsFileCreate, additionalStep: (JemsFileMetadataEntity) -> Unit) =
        fileService.persistProjectFileAndPerformAction(file = file, additionalStep = additionalStep)

    private fun JemsFileMetadataEntity?.deleteIfPresent() {
        if (this != null) {
            fileService.delete(this)
        }
    }

}
