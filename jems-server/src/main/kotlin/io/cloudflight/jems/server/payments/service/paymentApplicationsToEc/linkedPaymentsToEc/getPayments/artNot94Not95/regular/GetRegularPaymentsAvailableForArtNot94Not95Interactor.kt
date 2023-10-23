package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getPayments.artNot94Not95.regular

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetRegularPaymentsAvailableForArtNot94Not95Interactor {

    fun getPaymentList(pageable: Pageable, ecApplicationId: Long): Page<PaymentToEcPayment>
}
