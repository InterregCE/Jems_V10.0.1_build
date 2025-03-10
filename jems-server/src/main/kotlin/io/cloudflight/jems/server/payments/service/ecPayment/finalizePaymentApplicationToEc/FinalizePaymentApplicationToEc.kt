package io.cloudflight.jems.server.payments.service.ecPayment.finalizePaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.CorrectionInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.sumUpProperColumns
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcFinished
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinalizePaymentApplicationToEc(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
    private val ecPaymentCorrectionLinkPersistence: EcPaymentCorrectionLinkPersistence,
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : FinalizePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(FinalizePaymentApplicationToEcException::class)
    override fun finalizePaymentApplicationToEc(paymentId: Long): PaymentApplicationToEcDetail {
        val paymentApplication = ecPaymentPersistence.getPaymentApplicationToEcDetail(paymentId)
        validatePaymentApplicationIsDraft(paymentApplication.status)

        val selectedPaymentTotals = ecPaymentLinkPersistence.calculateAndGetOverviewForDraftEcPayment(paymentId).sumUpProperColumns()
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
                typologyProv94.fallsUnder94Or95() || typologyProv95.fallsUnder94Or95() -> PaymentSearchRequestScoBasis.FallsUnderArticle94Or95
                else -> PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
            }
        }
    }

    private fun Map<Long, CorrectionInEcPaymentMetadata>.toCorrectionFinalScoBasisChanges() = mapValues { (_, correctionMetadata) ->
        with (correctionMetadata) {
            when {
                typologyProv94.fallsUnder94Or95() || typologyProv95.fallsUnder94Or95() -> PaymentSearchRequestScoBasis.FallsUnderArticle94Or95
                else -> PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
            }
        }
    }

    private fun noOtherDraftEcPaymentExistsFor(fundId: Long, yearId: Long): Boolean =
        !ecPaymentPersistence.existsDraftByFundAndAccountingYear(fundId, accountingYearId = yearId)

    private fun accountingYearIsNotFinished(fundId: Long, yearId: Long): Boolean =
        !paymentAccountPersistence.findByFundAndYear(fundId = fundId, accountingYearId = yearId).status.isFinished()

    private fun PaymentApplicationToEcDetail.fillInFlagForReOpening() = apply {
        val fundId = this.paymentApplicationToEcSummary.programmeFund.id
        val accountingYearId = this.paymentApplicationToEcSummary.accountingYear.id

        this.isAvailableToReOpen = status.isFinished()
                && noOtherDraftEcPaymentExistsFor(fundId = fundId, yearId = accountingYearId)
                && accountingYearIsNotFinished(fundId = fundId, yearId = accountingYearId)
    }

    private fun ContractingMonitoringExtendedOption?.fallsUnder94Or95() = (this ?: ContractingMonitoringExtendedOption.No).isYes()

}
