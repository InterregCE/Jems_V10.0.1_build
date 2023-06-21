package io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class SetDescriptionToFile(
    private val generalValidator: GeneralValidatorService,
    private val projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence,
    private val fileService: JemsProjectFileService,
) : SetDescriptionToFileInteractor {


    @CanEditPartnerControlReportFile
    @Transactional
    @ExceptionWrapper(SetDescriptionToFileException::class)
    override fun setDescription(partnerId: Long, reportId: Long, fileId: Long, description: String) {
        validateDescription(text = description)

        val controlFile = projectPartnerReportControlFilePersistence.getByReportIdAndId(reportId, fileId)

        fileService.setDescription(fileId = controlFile.generatedFile.id, description = description)
    }


    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
