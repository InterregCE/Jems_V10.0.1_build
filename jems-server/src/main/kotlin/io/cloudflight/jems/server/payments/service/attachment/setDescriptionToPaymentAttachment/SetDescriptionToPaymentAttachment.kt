package io.cloudflight.jems.server.payments.service.attachment.setDescriptionToPaymentAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.minio.JemsProjectFileRepository
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType.PaymentAttachment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToPaymentAttachment(
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val fileRepository: JemsProjectFileRepository,
    private val generalValidator: GeneralValidatorService,
) : SetDescriptionToPaymentAttachmentInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(SetDescriptionToPaymentAttachmentException::class)
    override fun setDescription(fileId: Long, description: String) {
        validateDescription(text = description)

        if (!reportFilePersistence.existsFile(type = PaymentAttachment, fileId = fileId))
            throw FileNotFound()

        fileRepository.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }

}
