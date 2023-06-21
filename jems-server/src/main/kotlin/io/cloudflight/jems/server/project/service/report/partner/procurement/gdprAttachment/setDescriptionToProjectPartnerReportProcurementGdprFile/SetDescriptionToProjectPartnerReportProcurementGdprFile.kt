package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.setDescriptionToProjectPartnerReportProcurementGdprFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.ProcurementGdprAttachment
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToProjectPartnerReportProcurementGdprFile(
    private val partnerPersistence: PartnerPersistence,
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsProjectFileService,
    private val generalValidator: GeneralValidatorService,
    private val sensitiveDataAuthorization: SensitiveDataAuthorizationService
) : SetDescriptionToProjectPartnerReportProcurementGdprFileInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(SetDescriptionToProjectPartnerReportFileException::class)
    override fun setDescription(partnerId: Long, reportId: Long, fileId: Long, procurementId: Long, description: String) {
        if(!sensitiveDataAuthorization.canEditPartnerSensitiveData(partnerId)) {
            throw SensitiveFileException()
        }

        validateDescription(text = description)

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        // to make sure fileId corresponds to correct report, we need to verify it through location path
        val reportPrefix = ProcurementGdprAttachment.generatePath(projectId, partnerId, reportId, procurementId)

        if (!filePersistence.existsFile(partnerId = partnerId, pathPrefix = reportPrefix, fileId = fileId))
            throw FileNotFound()

        fileService.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
