package io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToCertificate

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class SetDescriptionToCertificate(
    private val generalValidator: GeneralValidatorService,
    private val partnerPersistence: PartnerPersistence,
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsProjectFileService,
): SetDescriptionToCertificateInteractor {


    @CanEditPartnerControlReportFile
    @Transactional
    @ExceptionWrapper(SetDescriptionToCertificateException::class)
    override fun setDescription(partnerId: Long, reportId: Long, fileId: Long, description: String) {
        validateDescription(text = description)

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        // to make sure fileId corresponds to correct report, we need to verify it through location path
        val reportPrefix = JemsFileType.ControlCertificate.generatePath(projectId, partnerId, reportId)

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