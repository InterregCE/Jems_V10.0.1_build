package io.cloudflight.jems.server.payments.service.account.attachment.setDescriptionToPaymentAccountAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToPaymentAccountAttachment(
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsSystemFileService,
    private val generalValidator: GeneralValidatorService,
) : SetDescriptionToPaymentAccountAttachmentInteractor {

    @CanUpdatePaymentsAccount
    @Transactional
    @ExceptionWrapper(SetDescriptionToPaymentToEcAttachmentException::class)
    override fun setDescription(fileId: Long, description: String) {
        validateDescription(text = description)

        if (!filePersistence.existsFile(type = JemsFileType.PaymentAccountAttachment, fileId = fileId))
            throw FileNotFound()

        fileService.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }

}
