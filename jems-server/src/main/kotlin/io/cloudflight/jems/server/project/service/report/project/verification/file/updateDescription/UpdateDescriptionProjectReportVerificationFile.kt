package io.cloudflight.jems.server.project.service.report.project.verification.file.updateDescription

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationCommunication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateDescriptionProjectReportVerificationFile(
    private val generalValidator: GeneralValidatorService,
    private val fileService: JemsProjectFileService,
    private val filePersistence: JemsFilePersistence,
) : UpdateDescriptionProjectReportVerificationFileInteractor {

    @CanEditReportVerificationCommunication
    @Transactional
    @ExceptionWrapper(UpdateDescriptionProjectReportVerificationFileException::class)
    override fun updateDescription(projectId: Long, reportId: Long, fileId: Long, description: String) {
        validateDescription(description)

        if (!filePersistence.existsFile(exactPath = VerificationDocument.generatePath(projectId, reportId), fileId = fileId))
            throw FileNotFound()

        fileService.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
