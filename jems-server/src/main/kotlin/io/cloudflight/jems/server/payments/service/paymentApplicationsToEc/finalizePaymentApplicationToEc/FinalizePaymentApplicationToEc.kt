package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.finalizePaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcStatusChanged
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcFinalized
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.sumUpProperColumns
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption.No
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinalizePaymentApplicationToEc(
    private val auditPublisher: ApplicationEventPublisher,
    private val paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence,
    private val paymentPersistence: PaymentPersistence,
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
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
        val projectIdPaymentIdsMap = mutableMapOf<Long, MutableSet<Long>>()
        linkedPayments.forEach { (paymentId) ->
            val projectId = paymentPersistence.getPaymentDetails(paymentId).projectId
            if (projectIdPaymentIdsMap.containsKey(projectId))
                projectIdPaymentIdsMap[projectId]?.add(paymentId)
            else
                projectIdPaymentIdsMap[projectId] = mutableSetOf(paymentId)
        }
        projectIdPaymentIdsMap.forEach { (projectId, paymentIds) ->
            val monitoring = contractingMonitoringPersistence.getContractingMonitoring(projectId)
            val scoBasis = if (monitoring.typologyProv94 == No && monitoring.typologyProv95 == No)
                PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95 else PaymentSearchRequestScoBasis.FallsUnderArticle94Or95
            paymentApplicationsToEcPersistence.updatePaymentToEcFinalScoBasis(paymentIds, scoBasis)
        }

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
