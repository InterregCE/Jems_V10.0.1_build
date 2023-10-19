package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.finalizePaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcStatusChanged
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
    override fun finalizePaymentApplicationToEc(paymentId: Long): PaymentApplicationToEcDetail {
        val paymentApplication = paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(paymentId)
        validatePaymentApplicationIsDraft(paymentApplication.status)

        val selectedPaymentTotals = paymentApplicationsToEcPersistence.calculateAndGetTotals(paymentId)
            .sumUpProperColumns()
        paymentApplicationsToEcPersistence.saveTotalsWhenFinishingEcPayment(paymentId, selectedPaymentTotals)

        return paymentApplicationsToEcPersistence.updatePaymentApplicationToEcStatus(paymentId, PaymentEcStatus.Finished)
            .apply {
                this.isAvailableToReOpen = !existsDraftPaymentApplicationWithFundAndAccountingYear(this.paymentApplicationToEcSummary)
            }.also {
                auditPublisher.publishEvent(
                    paymentApplicationToEcStatusChanged(
                        context = this,
                        updatedEcPaymentApplication = it,
                        previousStatus = paymentApplication.status,
                        paymentApplicationsToEcPersistence.getPaymentsLinkedToEcPayment(paymentId),
                    )
                )
            }
    }

    private fun validatePaymentApplicationIsDraft(ecPaymentApplicationStatus: PaymentEcStatus) {
        if (ecPaymentApplicationStatus != PaymentEcStatus.Draft)
            throw PaymentApplicationToEcNotInDraftException()
    }

    private fun existsDraftPaymentApplicationWithFundAndAccountingYear(paymentApplicationToEcSummary: PaymentApplicationToEcSummary): Boolean =
        paymentApplicationsToEcPersistence.existsDraftByFundAndAccountingYear(
            paymentApplicationToEcSummary.programmeFund.id, paymentApplicationToEcSummary.accountingYear.id
        )
}
