package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.createPaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcCreated
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreatePaymentApplicationToEc(
    private val paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence,
    private val paymentPersistence: PaymentPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : CreatePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(CreatePaymentApplicationToEcException::class)
    override fun createPaymentApplicationToEc(paymentApplicationToEc: PaymentApplicationToEcCreate): PaymentApplicationToEcDetail {
        validateFundAccountingYearPair(paymentApplicationToEc)
        val ecPayment = paymentApplicationsToEcPersistence.createPaymentApplicationToEc(paymentApplicationToEc)

        val fund = ecPayment.paymentApplicationToEcSummary.programmeFund
        val basis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95

        val ftlsPaymentIdsWithoutEcPayment = paymentPersistence
            .getPaymentIdsAvailableForEcPayments(fundId = fund.id, basis = basis)
        paymentApplicationsToEcPersistence.selectPaymentToEcPayment(ftlsPaymentIdsWithoutEcPayment, ecPayment.id)

        auditPublisher.publishEvent(paymentApplicationToEcCreated(context = this, ecPayment))
        return ecPayment
    }

    fun validateFundAccountingYearPair(paymentApplicationToEc: PaymentApplicationToEcCreate) {
        val existingEcPaymentApplication = paymentApplicationsToEcPersistence.existsDraftByFundAndAccountingYear(
                paymentApplicationToEc.programmeFundId,
                paymentApplicationToEc.accountingYearId
            )
        if (existingEcPaymentApplication) {
            throw EcPaymentApplicationSameFundAccountingYearExistsException()
        }
    }

}
