package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentsAvailableArtNot94Not95Interactor {

    fun getPaymentList(pageable: Pageable, ecPaymentId: Long, paymentType: PaymentType): Page<PaymentToEcPayment>
}
