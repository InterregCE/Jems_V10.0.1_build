package io.cloudflight.jems.server.payments.service.getAdvancePayments

import io.cloudflight.jems.server.payments.AdvancePaymentPersistence
import io.cloudflight.jems.server.payments.authorization.CanRetrieveAdvancePayments
import io.cloudflight.jems.server.payments.service.model.AdvancePayment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAdvancePayments(
    private val paymentPersistence: AdvancePaymentPersistence
): GetAdvancePaymentsInteractor {

    @CanRetrieveAdvancePayments
    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): Page<AdvancePayment> {
        return paymentPersistence.list(pageable)
    }
}
