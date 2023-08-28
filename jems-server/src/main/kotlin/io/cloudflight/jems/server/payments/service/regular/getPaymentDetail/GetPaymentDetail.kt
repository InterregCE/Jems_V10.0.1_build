package io.cloudflight.jems.server.payments.service.regular.getPaymentDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.payments.authorization.CanRetrievePayments
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentDetail(
    private val paymentPersistence: PaymentPersistence
): GetPaymentDetailInteractor {

    @CanRetrievePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentDetailException::class)
    override fun getPaymentDetail(paymentId: Long): PaymentDetail {
        return paymentPersistence.getPaymentDetails(paymentId)
    }
}
