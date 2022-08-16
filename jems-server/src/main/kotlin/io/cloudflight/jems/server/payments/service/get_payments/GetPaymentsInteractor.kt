package io.cloudflight.jems.server.payments.service.get_payments

import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.server.call.service.model.CallSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentsInteractor {

    fun getPayments(pageable: Pageable): Page<PaymentToProjectDTO>
}
