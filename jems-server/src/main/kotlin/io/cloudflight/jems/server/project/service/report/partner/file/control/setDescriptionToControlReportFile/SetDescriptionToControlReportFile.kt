package io.cloudflight.jems.server.project.service.report.partner.file.control.setDescriptionToControlReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.file.control.ControlReportFileAuthorizationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToControlReportFile(
    private val generalValidator: GeneralValidatorService,
    private val authorization: ControlReportFileAuthorizationService,
    private val reportFilePersistence: ProjectReportFilePersistence,
) : SetDescriptionToControlReportFileInteractor {

    @CanEditPartnerControlReportFile
    @Transactional
    @ExceptionWrapper(SetDescriptionToControlReportFileException::class)
    override fun setDescription(partnerId: Long, reportId: Long, fileId: Long, description: String) {
        validateDescription(text = description)
        authorization.validateChangeToFileAllowed(partnerId = partnerId, reportId = reportId, fileId)
        reportFilePersistence.setDescriptionToFile(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }

}
