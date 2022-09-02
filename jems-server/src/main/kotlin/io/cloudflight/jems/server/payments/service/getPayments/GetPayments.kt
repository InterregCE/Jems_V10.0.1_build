package io.cloudflight.jems.server.payments.service.getPayments

import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.entity.toDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GetPayments(private val paymentPersistence: PaymentPersistence) : GetPaymentsInteractor {

    override fun getPayments(pageable: Pageable): Page<PaymentToProjectDTO> {
        return paymentPersistence.getAllPaymentToProject(pageable).map { it.toDTO() }
    }
}
