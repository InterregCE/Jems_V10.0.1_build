package io.cloudflight.jems.server.payments.service.getPayments

import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GetPayments(private val paymentPersistence: PaymentPersistence) : GetPaymentsInteractor {

    override fun getPayments(pageable: Pageable): Page<PaymentToProject> {
        return paymentPersistence.getAllPaymentToProject(pageable)
    }
}
