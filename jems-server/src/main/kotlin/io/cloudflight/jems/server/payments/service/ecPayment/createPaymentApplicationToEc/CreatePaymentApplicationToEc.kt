package io.cloudflight.jems.server.payments.service.ecPayment.createPaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcCreated
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreatePaymentApplicationToEc(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
    private val paymentPersistence: PaymentPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : CreatePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(CreatePaymentApplicationToEcException::class)
    override fun createPaymentApplicationToEc(paymentApplicationToEc: PaymentApplicationToEcCreate): PaymentApplicationToEcDetail {
        validateFundAccountingYearPair(paymentApplicationToEc)
        val ecPayment = ecPaymentPersistence.createPaymentApplicationToEc(paymentApplicationToEc)

        val fund = ecPayment.paymentApplicationToEcSummary.programmeFund
        val accountingYear = ecPayment.paymentApplicationToEcSummary.accountingYear
        val basis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95

        val paymentIdsWithoutEcPayment = paymentPersistence.getPaymentIdsAvailableForEcPayments(fundId = fund.id, basis = basis)
        ecPaymentLinkPersistence.selectPaymentToEcPayment(paymentIdsWithoutEcPayment, ecPayment.id)

        saveCumulativeAmountsForPreviousFinishedFundAndAccountingYear(ecPayment.id, fund.id, accountingYear.id)

        auditPublisher.publishEvent(paymentApplicationToEcCreated(context = this, ecPayment))
        return ecPayment
    }

    fun validateFundAccountingYearPair(paymentApplicationToEc: PaymentApplicationToEcCreate) {
        val existingEcPaymentApplication = ecPaymentPersistence.existsDraftByFundAndAccountingYear(
                paymentApplicationToEc.programmeFundId,
                paymentApplicationToEc.accountingYearId
            )
        if (existingEcPaymentApplication) {
            throw EcPaymentApplicationSameFundAccountingYearExistsException()
        }
    }


    fun saveCumulativeAmountsForPreviousFinishedFundAndAccountingYear(ecPaymentId: Long, programmeFundId: Long, accountingYearId: Long) {
        val cumulativeAmounts = ecPaymentLinkPersistence.getCumulativeOverviewForFundAndYear(programmeFundId, accountingYearId)
        ecPaymentLinkPersistence.saveCumulativeAmounts(ecPaymentId, cumulativeAmounts)
    }

}
