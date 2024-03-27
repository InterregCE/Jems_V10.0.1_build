package io.cloudflight.jems.server.payments.service.ecPayment.reOpenFinalizedEcPaymentApplication

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcReOpened
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReOpenFinalizedEcPaymentApplication(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val auditPublisher: ApplicationEventPublisher,
): ReOpenFinalizedEcPaymentApplicationInteractor {

    @Transactional
    @CanUpdatePaymentApplicationsToEc
    @ExceptionWrapper(ReOpenFinalizedEcPaymentApplicationException::class)
    override fun reOpen(ecPaymentApplicationId: Long): PaymentApplicationToEcDetail {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(ecPaymentApplicationId)

        if (!ecPayment.status.isFinished())
            throw EcPaymentNotFinishedException()

        validateNoOtherEcPaymentIsOpenForThisFundAndYear(fundId = ecPayment.fundId(), yearId = ecPayment.yearId())
        validateAccountingYearIsNotFinished(fundId = ecPayment.fundId(), yearId = ecPayment.yearId())

        return ecPaymentPersistence.updatePaymentApplicationToEcStatus(ecPaymentApplicationId, PaymentEcStatus.Draft).also {
            auditPublisher.publishEvent(paymentApplicationToEcReOpened(context = this, it))
        }
    }

    private fun PaymentApplicationToEcDetail.fundId() =
        paymentApplicationToEcSummary.programmeFund.id

    private fun PaymentApplicationToEcDetail.yearId() =
        paymentApplicationToEcSummary.accountingYear.id

    private fun validateNoOtherEcPaymentIsOpenForThisFundAndYear(fundId: Long, yearId: Long) {
        val thereIsAlreadyOtherDraftEcPayment = ecPaymentPersistence
            .existsDraftByFundAndAccountingYear(programmeFundId = fundId, accountingYearId = yearId)
        if (thereIsAlreadyOtherDraftEcPayment) {
            throw ThereIsOtherEcPaymentInDraftException()
        }
    }

    private fun validateAccountingYearIsNotFinished(fundId: Long, yearId: Long) {
        val account = paymentAccountPersistence.findByFundAndYear(fundId = fundId, accountingYearId = yearId)
        if (account.status.isFinished())
            throw AccountingYearHasBeenAlreadyFinishedException()
    }

}
