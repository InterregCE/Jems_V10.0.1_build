package io.cloudflight.jems.server.project.service.report.partner.file.control.setDescriptionToControlReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.file.control.ControlReportFileAuthorizationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToControlReportFile(
    private val generalValidator: GeneralValidatorService,
    private val authorization: ControlReportFileAuthorizationService,
    private val fileService: JemsProjectFileService,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val sensitiveDataAuthorization: SensitiveDataAuthorizationService
) : SetDescriptionToControlReportFileInteractor {

    @CanEditPartnerControlReportFile
    @Transactional
    @ExceptionWrapper(SetDescriptionToControlReportFileException::class)
    override fun setDescription(partnerId: Long, reportId: Long, fileId: Long, description: String) {
        validateDescription(text = description)

        if(isGdprProtected(fileId = fileId, partnerId = partnerId) &&
            !sensitiveDataAuthorization.canEditPartnerSensitiveData(partnerId)) {
            throw SensitiveFileException()
        }

        authorization.validateChangeToFileAllowed(partnerId = partnerId, reportId = reportId, fileId, false)
        fileService.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }

    private fun isGdprProtected(fileId: Long, partnerId: Long) =
        reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(partnerId = partnerId, fileId = fileId)

}
