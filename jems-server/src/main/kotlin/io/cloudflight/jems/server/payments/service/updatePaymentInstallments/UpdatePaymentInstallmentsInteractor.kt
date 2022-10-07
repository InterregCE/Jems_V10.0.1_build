package io.cloudflight.jems.server.payments.service.updatePaymentInstallments

import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallmentUpdate

interface UpdatePaymentInstallmentsInteractor {

    fun updatePaymentPartnerInstallments(
        paymentId: Long,
        partnerId: Long,
        installments: List<PaymentPartnerInstallmentUpdate>
    ): List<PaymentPartnerInstallment>
}
