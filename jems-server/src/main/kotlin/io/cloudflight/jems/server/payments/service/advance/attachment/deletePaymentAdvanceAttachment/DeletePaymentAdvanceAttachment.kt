package io.cloudflight.jems.server.payments.service.advance.attachment.deletePaymentAdvanceAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdateAdvancePayments
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentAdvanceAttachment(
    private val paymentPersistence: PaymentAdvancePersistence,
): DeletePaymentAdvAttachmentInteractor {

    @CanUpdateAdvancePayments
    @Transactional
    @ExceptionWrapper(DeletePaymentAdvAttachmentException::class)
    override fun delete(fileId: Long) =
        paymentPersistence.deletePaymentAdvanceAttachment(fileId)
}