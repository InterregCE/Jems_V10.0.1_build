package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentApplicationsToEcPersistence {

    fun updatePaymentApplicationsToEc(paymentApplicationsToEcUpdate: PaymentApplicationsToEcUpdate): PaymentApplicationsToEcDetail

    fun getPaymentApplicationsToEcDetail(id: Long): PaymentApplicationsToEcDetail

    fun findAll(pageable: Pageable): Page<PaymentApplicationsToEc>

    fun deleteById(id: Long)
}
