package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.PaymentsApi
import io.cloudflight.jems.api.payments.dto.PaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentToProjectDTO
import io.cloudflight.jems.server.payments.service.regular.getPaymentDetail.GetPaymentDetailInteractor
import io.cloudflight.jems.server.payments.service.regular.getPayments.GetPaymentsInteractor
import io.cloudflight.jems.server.payments.service.regular.updatePaymentInstallments.UpdatePaymentInstallmentsInteractor
import io.cloudflight.jems.server.payments.service.toDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentsController(
    private val getPayments: GetPaymentsInteractor,
    private val getPaymentDetail: GetPaymentDetailInteractor,
    private val updatePaymentInstallments: UpdatePaymentInstallmentsInteractor
): PaymentsApi {

    override fun getPaymentsToProjects(pageable: Pageable): Page<PaymentToProjectDTO> {
        return getPayments.getPayments(pageable).map { it.toDTO() }
    }

    override fun getPaymentDetail(paymentId: Long): PaymentDetailDTO {
        return getPaymentDetail.getPaymentDetail(paymentId).toDTO()
    }

    override fun updatePaymentInstallments(
        paymentId: Long,
        paymentDetail: PaymentDetailDTO
    ): PaymentDetailDTO {
        return updatePaymentInstallments
            .updatePaymentInstallments(
                paymentId = paymentId,
                paymentDetail = paymentDetail
            ).toDTO()
    }
}
