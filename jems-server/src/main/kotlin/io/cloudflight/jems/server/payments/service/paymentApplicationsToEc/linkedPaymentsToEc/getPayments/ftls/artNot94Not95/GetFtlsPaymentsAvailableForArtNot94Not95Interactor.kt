package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getPayments.ftls.artNot94Not95

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetFtlsPaymentsAvailableForArtNot94Not95Interactor {

    fun getPaymentList(pageable: Pageable, ecApplicationId: Long): Page<PaymentToEcPayment>

}
