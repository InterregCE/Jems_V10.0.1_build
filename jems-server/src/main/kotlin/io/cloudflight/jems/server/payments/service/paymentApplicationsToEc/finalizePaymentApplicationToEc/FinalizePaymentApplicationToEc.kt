package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.finalizePaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcReOpened
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

        val linkedPayments = paymentApplicationsToEcPersistence.getPaymentsLinkedToEcPayment(paymentId)

        val finalScoBasisPerPayment = linkedPayments.toFinalScoBasisChanges()
        paymentApplicationsToEcPersistence.updatePaymentToEcFinalScoBasis(finalScoBasisPerPayment)

        val updatedModel = paymentApplicationsToEcPersistence.updatePaymentApplicationToEcStatus(paymentId, PaymentEcStatus.Finished)
        updatedModel.fillInFlagForReOpening()

        return updatedModel.also {
            auditPublisher.publishEvent(paymentApplicationToEcReOpened(context = this, it))
        }
    }

    private fun validatePaymentApplicationIsDraft(ecPaymentApplicationStatus: PaymentEcStatus) {
        if (ecPaymentApplicationStatus != PaymentEcStatus.Draft)
            throw PaymentApplicationToEcNotInDraftException()
    }

    private fun Map<Long, PaymentInEcPaymentMetadata>.toFinalScoBasisChanges() = mapValues { (_, paymentMetadata) ->
        with (paymentMetadata) {
            when {
                finalScoBasis != null -> finalScoBasis
                typologyProv94.isNo() && typologyProv94.isNo() -> PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
                else -> PaymentSearchRequestScoBasis.FallsUnderArticle94Or95
            }
        }
    }

    private fun noOtherDraftEcPaymentExistsFor(paymentToEc: PaymentApplicationToEcSummary): Boolean =
        !paymentApplicationsToEcPersistence.existsDraftByFundAndAccountingYear(
            programmeFundId = paymentToEc.programmeFund.id,
            accountingYearId = paymentToEc.accountingYear.id,
        )

    private fun PaymentApplicationToEcDetail.fillInFlagForReOpening() = apply {
        this.isAvailableToReOpen = status.isFinished() && noOtherDraftEcPaymentExistsFor(paymentApplicationToEcSummary)
    }

}
