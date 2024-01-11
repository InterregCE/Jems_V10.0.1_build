package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95.ftls

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetFtlsPaymentsAvailableForArtNot94Not95Interactor {

    fun getPaymentList(pageable: Pageable, ecPaymentId: Long): Page<PaymentToEcPayment>

}
