package io.cloudflight.jems.server.payments.service.ecPayment.finalizePaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.CorrectionInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.sumUpProperColumns
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcFinished
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinalizePaymentApplicationToEc(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
    private val ecPaymentCorrectionLinkPersistence: EcPaymentCorrectionLinkPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : FinalizePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(FinalizePaymentApplicationToEcException::class)
    override fun finalizePaymentApplicationToEc(paymentId: Long): PaymentApplicationToEcDetail {
        val paymentApplication = ecPaymentPersistence.getPaymentApplicationToEcDetail(paymentId)
        validatePaymentApplicationIsDraft(paymentApplication.status)

        val selectedPaymentTotals = ecPaymentLinkPersistence.calculateAndGetOverview(paymentId).sumUpProperColumns()
        ecPaymentLinkPersistence.saveTotalsWhenFinishingEcPayment(paymentId, selectedPaymentTotals)

        val linkedPayments = ecPaymentLinkPersistence.getPaymentsLinkedToEcPayment(paymentId)
        val linkedCorrections = ecPaymentCorrectionLinkPersistence.getCorrectionsLinkedToEcPayment(paymentId)

        val finalScoBasisPerPayment = linkedPayments.toFinalScoBasisChanges()
        ecPaymentLinkPersistence.updatePaymentToEcFinalScoBasis(finalScoBasisPerPayment)

        val finalScoBasisPerCorrection = linkedCorrections.toCorrectionFinalScoBasisChanges()
        ecPaymentCorrectionLinkPersistence.updatePaymentToEcFinalScoBasis(finalScoBasisPerCorrection)

        val updatedModel = ecPaymentPersistence.updatePaymentApplicationToEcStatus(paymentId, PaymentEcStatus.Finished)
        updatedModel.fillInFlagForReOpening()

        return updatedModel.also {
            auditPublisher.publishEvent(paymentApplicationToEcFinished(context = this, it, linkedPayments, linkedCorrections))
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

    private fun Map<Long, CorrectionInEcPaymentMetadata>.toCorrectionFinalScoBasisChanges() = mapValues { (_, correctionMetadata) ->
        with (correctionMetadata) {
            when {
                finalScoBasis != null -> finalScoBasis
                typologyProv94.isNo() && typologyProv94.isNo() -> PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
                else -> PaymentSearchRequestScoBasis.FallsUnderArticle94Or95
            }
        }
    }

    private fun noOtherDraftEcPaymentExistsFor(paymentToEc: PaymentApplicationToEcSummary): Boolean =
        !ecPaymentPersistence.existsDraftByFundAndAccountingYear(
            programmeFundId = paymentToEc.programmeFund.id,
            accountingYearId = paymentToEc.accountingYear.id,
        )

    private fun PaymentApplicationToEcDetail.fillInFlagForReOpening() = apply {
        this.isAvailableToReOpen = status.isFinished() && noOtherDraftEcPaymentExistsFor(paymentApplicationToEcSummary)
    }

}
