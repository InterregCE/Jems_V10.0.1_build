package io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.setDescriptionToPaymentAuditAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToPaymentAuditAttachment(
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsSystemFileService,
    private val generalValidator: GeneralValidatorService,
) : SetDescriptionToPaymentAuditAttachmentInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(SetDescriptionToPaymentAttachmentException::class)
    override fun setDescription(fileId: Long, description: String) {
        validateDescription(text = description)

        if (!filePersistence.existsFile(type = JemsFileType.PaymentAuditAttachment, fileId = fileId))
            throw FileNotFound()

        fileService.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }

}
