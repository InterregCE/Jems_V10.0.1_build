package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.finalizePaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcFinalized
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.sumUpProperColumns
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinalizePaymentApplicationToEc(
    private val auditPublisher: ApplicationEventPublisher,
    private val paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence,
) : FinalizePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(FinalizePaymentApplicationToEcException::class)
    override fun finalizePaymentApplicationToEc(paymentId: Long): PaymentEcStatus {
        val ecPayment = paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(paymentId)
        validatePaymentApplicationIsDraft(ecPayment)

        val selectedPaymentTotals = paymentApplicationsToEcPersistence.getSelectedPaymentsToEcPayment(paymentId)
            .sumUpProperColumns()
        paymentApplicationsToEcPersistence.saveCumulativeAmountsByType(paymentId, selectedPaymentTotals)

        return paymentApplicationsToEcPersistence.finalizePaymentApplicationToEc(paymentId).also {
            auditPublisher.publishEvent(paymentApplicationToEcFinalized(
                context = this,
                paymentApplicationToEc = it,
                paymentApplicationsToEcPersistence.getPaymentsLinkedToEcPayment(paymentId),
            ))
        }.status
    }

    private fun validatePaymentApplicationIsDraft(payment: PaymentApplicationToEcDetail) {
        if (payment.status != PaymentEcStatus.Draft)
            throw PaymentApplicationToEcNotInDraftException()
    }

}
