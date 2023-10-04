package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentType
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

    fun getPaymentExtension(paymentId: Long): PaymentToEcExtension

    fun getPaymentsLinkedToEcPayment(ecPaymentId: Long): Map<Long, PaymentType>

    fun selectPaymentToEcPayment(paymentIds: Set<Long>, ecPaymentId: Long)

    fun deselectPaymentFromEcPaymentAndResetFields(paymentId: Long)

    fun updatePaymentToEcCorrectedAmounts(paymentId: Long, paymentToEcLinkingUpdate: PaymentToEcLinkingUpdate)

}
