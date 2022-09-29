package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.PaymentDetailDTO
import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.PaymentsApi
import io.cloudflight.jems.server.payments.service.toDTO
import io.cloudflight.jems.server.payments.service.getPaymentDetail.GetPaymentDetailInteractor
import io.cloudflight.jems.server.payments.service.getPayments.GetPaymentsInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentsController(
    private val getPayments: GetPaymentsInteractor,
    private val getPaymentDetail: GetPaymentDetailInteractor
): PaymentsApi {

    override fun getPaymentsToProjects(pageable: Pageable): Page<PaymentToProjectDTO> {
        return getPayments.getPayments(pageable).map { it.toDTO() }
    }

    override fun getPaymentDetail(paymentId: Long): PaymentDetailDTO {
        return getPaymentDetail.getPaymentDetail(paymentId).toDTO()
    }
}
