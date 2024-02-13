package io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentApplicationToEcDetail(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val paymentAccountPersistence: PaymentAccountPersistence,
) : GetPaymentApplicationToEcDetailInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentApplicationToEcDetailException::class)
    override fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetail =
        ecPaymentPersistence.getPaymentApplicationToEcDetail(id).fillInFlagForReOpening()

    private fun PaymentApplicationToEcDetail.fillInFlagForReOpening() = apply {
        val fundId = this.paymentApplicationToEcSummary.programmeFund.id
        val accountingYearId = this.paymentApplicationToEcSummary.accountingYear.id

        this.isAvailableToReOpen = status.isFinished()
                && noOtherDraftEcPaymentExistsFor(fundId = fundId, yearId = accountingYearId)
                && accountingYearIsNotFinished(fundId = fundId, yearId = accountingYearId)
    }

    private fun noOtherDraftEcPaymentExistsFor(fundId: Long, yearId: Long): Boolean =
        !ecPaymentPersistence.existsDraftByFundAndAccountingYear(fundId, accountingYearId = yearId)

    private fun accountingYearIsNotFinished(fundId: Long, yearId: Long): Boolean =
        !paymentAccountPersistence.findByFundAndYear(fundId = fundId, accountingYearId = yearId).status.isFinished()

}
