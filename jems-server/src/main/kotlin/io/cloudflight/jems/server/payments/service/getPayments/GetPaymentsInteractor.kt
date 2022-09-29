package io.cloudflight.jems.server.payments.service.getPayments

import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentsInteractor {

    fun getPayments(pageable: Pageable): Page<PaymentToProject>
}
