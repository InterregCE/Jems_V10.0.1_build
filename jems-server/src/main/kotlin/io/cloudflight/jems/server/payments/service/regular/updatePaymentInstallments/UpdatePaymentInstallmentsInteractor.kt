package io.cloudflight.jems.server.payments.service.regular.updatePaymentInstallments

import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate

interface UpdatePaymentInstallmentsInteractor {

    fun updatePaymentPartnerInstallments(
        paymentId: Long,
        partnerId: Long,
        installments: List<PaymentPartnerInstallmentUpdate>
    ): List<PaymentPartnerInstallment>
}
