package io.cloudflight.jems.server.payments.service.account.attachment.deletePaymentAccountAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentAccountAttachment(
    private val paymentAccountPersistence: PaymentAccountPersistence
) : DeletePaymentAccountAttachmentInteractor {

    @CanUpdatePaymentsAccount
    @Transactional
    @ExceptionWrapper(DeletePaymentAccountAttachmentException::class)
    override fun delete(fileId: Long) = paymentAccountPersistence.deletePaymentAccountAttachment(fileId)

}
