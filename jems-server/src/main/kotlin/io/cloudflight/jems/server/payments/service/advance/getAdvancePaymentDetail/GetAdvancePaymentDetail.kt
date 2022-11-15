package io.cloudflight.jems.server.payments.service.advance.getAdvancePaymentDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.payments.authorization.CanRetrieveAdvancePayments
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAdvancePaymentDetail(
    private val paymentPersistence: PaymentAdvancePersistence
): GetAdvancePaymentDetailInteractor {

    @CanRetrieveAdvancePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAdvancePaymentDetailException::class)
    override fun getPaymentDetail(paymentId: Long): AdvancePaymentDetail {
        return paymentPersistence.getPaymentDetail(paymentId)
    }

}
