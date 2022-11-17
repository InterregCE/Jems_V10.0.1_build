package io.cloudflight.jems.server.payments.service.advance.attachment.setDescriptionToPaymentAdvAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.minio.JemsProjectFileRepository
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.authorization.CanUpdateAdvancePayments
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType.PaymentAdvanceAttachment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToPaymentAdvAttachment(
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val fileRepository: JemsProjectFileRepository,
    private val generalValidator: GeneralValidatorService,
): SetDescriptionToPaymentAdvAttachmentInteractor {

    @CanUpdateAdvancePayments
    @Transactional
    @ExceptionWrapper(SetDescriptionToPaymentAdvAttachmentException::class)
    override fun setDescription(fileId: Long, description: String) {
        validateDescription(text = description)

        if (!reportFilePersistence.existsFile(type = PaymentAdvanceAttachment, fileId = fileId))
            throw FileNotFound()

        fileRepository.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }

}