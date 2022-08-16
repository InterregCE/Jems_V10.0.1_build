package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.PaymentsApi
import io.cloudflight.jems.server.payments.service.get_payments.GetPaymentsInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentsController(
    private val getPayments: GetPaymentsInteractor
): PaymentsApi {

    override fun getPaymentsToProjects(pageable: Pageable): Page<PaymentToProjectDTO> {
        return getPayments.getPayments(pageable)
    }
}
