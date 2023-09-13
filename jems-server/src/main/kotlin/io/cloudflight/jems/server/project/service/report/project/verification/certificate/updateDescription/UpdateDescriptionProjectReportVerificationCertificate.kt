package io.cloudflight.jems.server.project.service.report.project.verification.certificate.updateDescription

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationCertificate
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationPrivileged
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateDescriptionProjectReportVerificationCertificate(
    private val generalValidator: GeneralValidatorService,
    private val fileService: JemsProjectFileService,
    private val filePersistence: JemsFilePersistence,
) : UpdateDescriptionProjectReportVerificationCertificateInteractor {

    @CanEditReportVerificationPrivileged
    @Transactional
    @ExceptionWrapper(UpdateDescriptionProjectReportVerificationCertificateException::class)
    override fun updateDescription(projectId: Long, reportId: Long, fileId: Long, description: String) {
        validateDescription(description)

        if (!filePersistence.existsFile(exactPath = VerificationCertificate.generatePath(projectId, reportId), fileId = fileId))
            throw FileNotFound()

        fileService.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
