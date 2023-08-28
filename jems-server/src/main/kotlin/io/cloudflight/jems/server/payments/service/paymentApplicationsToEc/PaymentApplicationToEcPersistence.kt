package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentApplicationToEcPersistence {

    fun createPaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail

    fun updatePaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail

    fun updatePaymentToEcSummaryOtherSection(paymentToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail

    fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetail

    fun findAll(pageable: Pageable): Page<PaymentApplicationToEc>

    fun finalizePaymentApplicationToEc(paymentId: Long): PaymentApplicationToEcDetail

    fun deleteById(id: Long)

    fun deletePaymentToEcAttachment(fileId: Long)
}
