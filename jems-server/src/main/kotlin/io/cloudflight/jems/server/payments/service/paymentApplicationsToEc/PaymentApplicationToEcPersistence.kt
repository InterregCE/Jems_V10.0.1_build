package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentApplicationToEcPersistence {

    fun createPaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcUpdate): PaymentApplicationToEcDetail

    fun updatePaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcUpdate): PaymentApplicationToEcDetail

    fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetail

    fun findAll(pageable: Pageable): Page<PaymentApplicationToEc>

    fun deleteById(id: Long)
}
