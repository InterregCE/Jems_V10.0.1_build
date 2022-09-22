package io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.PartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToProjectPartnerReportFile(
    private val partnerPersistence: PartnerPersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val generalValidator: GeneralValidatorService,
) : SetDescriptionToProjectPartnerReportFileInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(SetDescriptionToProjectPartnerReportFileException::class)
    override fun setDescription(partnerId: Long, reportId: Long, fileId: Long, description: String) {
        validateDescription(text = description)

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        // to make sure fileId corresponds to correct report, we need to verify it through location path
        val reportPrefix = PartnerReport.generatePath(projectId, partnerId, reportId)

        if (!reportFilePersistence.existsFile(partnerId = partnerId, pathPrefix = reportPrefix, fileId = fileId))
            throw FileNotFound()

        reportFilePersistence.setDescriptionToFile(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }

}
