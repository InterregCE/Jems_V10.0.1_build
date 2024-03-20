package io.cloudflight.jems.server.payments.service.regular.updatePaymentInstallments

import io.cloudflight.jems.api.payments.dto.PaymentPartnerDTO
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail

interface UpdatePaymentInstallmentsInteractor {

    fun updatePaymentInstallments(
        paymentId: Long,
        partnerPayments: List<PaymentPartnerDTO>,
    ): PaymentDetail
}
