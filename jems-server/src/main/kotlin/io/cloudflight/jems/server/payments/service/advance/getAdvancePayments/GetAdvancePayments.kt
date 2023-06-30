package io.cloudflight.jems.server.payments.service.advance.getAdvancePayments

import io.cloudflight.jems.server.payments.authorization.CanRetrieveAdvancePayments
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.payments.service.advance.calculateAmountSettled
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAdvancePayments(
    private val paymentPersistence: PaymentAdvancePersistence
): GetAdvancePaymentsInteractor {

    @CanRetrieveAdvancePayments
    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, filters: AdvancePaymentSearchRequest): Page<AdvancePayment> {
        return paymentPersistence.list(pageable, filters).also {
            it.content.forEach { payment -> payment.calculateAmountSettled() }
        }
    }
}
