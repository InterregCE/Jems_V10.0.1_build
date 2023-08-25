package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.attachment.deletePaymentToEcAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentToEcAttachment(
    private val paymentToEcPersistence: PaymentApplicationToEcPersistence
) : DeletePaymentToEcAttachmentInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(DeletePaymentToEcAttachmentException::class)
    override fun delete(fileId: Long) = paymentToEcPersistence.deletePaymentToEcAttachment(fileId)
}
