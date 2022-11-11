package io.cloudflight.jems.server.payments.service.attachment.deletePaymentAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentAttachment(
    private val paymentPersistence: PaymentPersistence,
) : DeletePaymentAttachmentInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(DeletePaymentAttachmentException::class)
    override fun delete(fileId: Long) = paymentPersistence.deletePaymentAttachment(fileId)

}
