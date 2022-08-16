package io.cloudflight.jems.server.payments.service.get_payments

import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GetPayments() : GetPaymentsInteractor {
    override fun getPayments(pageable: Pageable): Page<PaymentToProjectDTO> {
        TODO("Not yet implemented")
    }
}
