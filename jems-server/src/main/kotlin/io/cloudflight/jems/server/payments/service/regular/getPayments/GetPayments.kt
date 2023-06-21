package io.cloudflight.jems.server.payments.service.regular.getPayments

import io.cloudflight.jems.server.payments.service.regular.PaymentRegularPersistence
import io.cloudflight.jems.server.payments.authorization.CanRetrievePayments
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPayments(private val paymentPersistence: PaymentRegularPersistence) : GetPaymentsInteractor {

    @CanRetrievePayments
    @Transactional(readOnly = true)
    override fun getPayments(pageable: Pageable, filters: PaymentSearchRequest): Page<PaymentToProject> {
        return paymentPersistence.getAllPaymentToProject(pageable, filters)
    }
}
