package io.cloudflight.jems.server.payments.service.getPaymentDetail

import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.service.model.PaymentDetail
import org.springframework.stereotype.Service

@Service
class GetPaymentDetail(
    private val paymentPersistence: PaymentPersistence
): GetPaymentDetailInteractor {

    override fun getPaymentDetail(paymentId: Long): PaymentDetail {
        return paymentPersistence.getPaymentDetails(paymentId)
    }
}
