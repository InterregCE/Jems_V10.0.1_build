package io.cloudflight.jems.server.payments.service.attachment.setDescriptionToPaymentAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.minio.GenericPaymentFileRepository
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.PaymentAttachment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToPaymentAttachment(
    private val genericFileRepository: GenericPaymentFileRepository,
    private val generalValidator: GeneralValidatorService,
) : SetDescriptionToPaymentAttachmentInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(SetDescriptionToPaymentAttachmentException::class)
    override fun setDescription(fileId: Long, description: String) {
        validateDescription(text = description)
        genericFileRepository.setDescription(PaymentAttachment, fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }

}
