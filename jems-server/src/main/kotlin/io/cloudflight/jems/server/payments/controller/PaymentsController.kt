package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.PaymentDetailDTO
import io.cloudflight.jems.api.payments.PaymentPartnerInstallmentDTO
import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.PaymentsApi
import io.cloudflight.jems.server.payments.service.getPaymentDetail.GetPaymentDetailInteractor
import io.cloudflight.jems.server.payments.service.getPayments.GetPaymentsInteractor
import io.cloudflight.jems.server.payments.service.toDTO
import io.cloudflight.jems.server.payments.service.toModelList
import io.cloudflight.jems.server.payments.service.updatePaymentInstallments.UpdatePaymentInstallmentsInteractor
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

    override fun updatePaymentPartnerInstallments(
        paymentId: Long,
        partnerId: Long,
        installments: List<PaymentPartnerInstallmentDTO>
    ): List<PaymentPartnerInstallmentDTO> {
        return updatePaymentInstallments
            .updatePaymentPartnerInstallments(
                paymentId = paymentId,
                partnerId = partnerId,
                installments = installments.toModelList()
            ).map { it.toDTO() }
    }
}
