package io.cloudflight.jems.server.payments.service.getAdvancePaymentDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.AdvancePaymentPersistence
import io.cloudflight.jems.server.payments.authorization.CanRetrieveAdvancePayments
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAdvancePaymentDetail(
    private val paymentPersistence: AdvancePaymentPersistence
): GetAdvancePaymentDetailInteractor {

    @CanRetrieveAdvancePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAdvancePaymentDetailException::class)
    override fun getPaymentDetail(paymentId: Long): AdvancePaymentDetail {
        return paymentPersistence.getPaymentDetail(paymentId)
    }

}
