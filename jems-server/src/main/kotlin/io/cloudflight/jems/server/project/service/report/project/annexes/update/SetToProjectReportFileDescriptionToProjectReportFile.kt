package io.cloudflight.jems.server.project.service.report.project.annexes.update

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetToProjectReportFileDescriptionToProjectReportFile(
    private val generalValidator: GeneralValidatorService,
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsProjectFileService,
) : SetDescriptionToProjectReportFileInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(SetDescriptionToProjectReportFileException::class)
    override fun update(projectId: Long, reportId: Long, fileId: Long, description: String) {
        validateDescription(description)

        val projectReportPrefix = JemsFileType.ProjectReport.generatePath(projectId, reportId)
        if (!filePersistence.existsReportFile(projectId, projectReportPrefix, fileId))
            throw FileNotFound()

        fileService.setDescription(fileId, description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
