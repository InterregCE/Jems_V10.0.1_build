package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.attachment.setDescriptionToPaymentToEcAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.PaymentToEcAttachment
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToPaymentToEcAttachment(
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsSystemFileService,
    private val generalValidator: GeneralValidatorService,
) : SetDescriptionToPaymentToEcAttachmentInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(SetDescriptionToPaymentToEcAttachmentException::class)
    override fun setDescription(fileId: Long, description: String) {
        validateDescription(text = description)

        if (!filePersistence.existsFile(type = PaymentToEcAttachment, fileId = fileId))
            throw FileNotFound()

        fileService.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }

}
