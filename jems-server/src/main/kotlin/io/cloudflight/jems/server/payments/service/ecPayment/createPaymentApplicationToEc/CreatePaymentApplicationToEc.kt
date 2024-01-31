package io.cloudflight.jems.server.payments.service.ecPayment.createPaymentApplicationToEc

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
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
    private val ecPaymentCorrectionLinkPersistence: EcPaymentCorrectionLinkPersistence,
    private val paymentPersistence: PaymentPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : CreatePaymentApplicationToEcInteractor {

    @CanUpdatePaymentApplicationsToEc
    @Transactional
    @ExceptionWrapper(CreatePaymentApplicationToEcException::class)
    override fun createPaymentApplicationToEc(paymentApplicationToEc: PaymentApplicationToEcCreate): PaymentApplicationToEcDetail {
        validateNoOtherDraftExist(paymentApplicationToEc)
        val ecPayment = ecPaymentPersistence.createPaymentApplicationToEc(paymentApplicationToEc)

        val fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id
        val yearId = ecPayment.paymentApplicationToEcSummary.accountingYear.id

        preSelectAllAvailablePayments(ecPayment.id, fundId = fundId)
        preSelectAllAvailableCorrections(ecPayment.id, fundId = fundId)
        storeCumulativeValues(ecPayment.id, fundId = fundId, yearId = yearId)

        auditPublisher.publishEvent(paymentApplicationToEcCreated(context = this, ecPayment))
        return ecPayment
    }

    fun validateNoOtherDraftExist(paymentApplicationToEc: PaymentApplicationToEcCreate) {
        val existingEcPaymentApplication = ecPaymentPersistence.existsDraftByFundAndAccountingYear(
                paymentApplicationToEc.programmeFundId,
                paymentApplicationToEc.accountingYearId
            )
        if (existingEcPaymentApplication) {
            throw EcPaymentApplicationSameFundAccountingYearExistsException()
        }
    }

    fun preSelectAllAvailablePayments(ecPaymentId: Long, fundId: Long) {
        val paymentIdsWithoutEcPayment = paymentPersistence.getPaymentIdsAvailableForEcPayments(fundId = fundId)
        ecPaymentLinkPersistence.selectPaymentToEcPayment(paymentIdsWithoutEcPayment, ecPaymentId)
    }

    fun preSelectAllAvailableCorrections(ecPaymentId: Long, fundId: Long) {
        val correctionIdsWithoutEcPayment = ecPaymentCorrectionLinkPersistence.getCorrectionIdsAvailableForEcPayments(fundId = fundId)
        ecPaymentCorrectionLinkPersistence.selectCorrectionToEcPayment(correctionIdsWithoutEcPayment, ecPaymentId)
    }

    fun storeCumulativeValues(ecPaymentId: Long, fundId: Long, yearId: Long) {
        val finishedEcPaymentIds = ecPaymentPersistence.getIdsFinishedForYearAndFund(yearId, fundId = fundId)
        val cumulativeAmounts = ecPaymentLinkPersistence.getCumulativeAmounts(finishedEcPaymentIds)
        ecPaymentLinkPersistence.saveCumulativeAmounts(ecPaymentId, cumulativeAmounts)
    }

}
