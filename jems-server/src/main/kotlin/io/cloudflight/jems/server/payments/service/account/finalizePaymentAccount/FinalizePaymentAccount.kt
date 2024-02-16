package io.cloudflight.jems.server.payments.service.account.finalizePaymentAccount

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.payments.service.account.finance.correction.sumUpProperColumns
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.paymentAccountsFinished
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinalizePaymentAccount(
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : FinalizePaymentAccountInteractor {

    @CanUpdatePaymentsAccount
    @Transactional
    @ExceptionWrapper(FinalizePaymentAccountException::class)
    override fun finalizePaymentAccount(paymentAccountId: Long): PaymentAccountStatus {
        val paymentAccount = paymentAccountPersistence.getByPaymentAccountId(paymentAccountId)

        validateAccountIsDraft(paymentAccount)
        validateNoEcPaymentForAccountingYearIsDraft(paymentAccount)

        val selectedPaymentTotals = correctionLinkingPersistence.calculateOverviewForDraftPaymentAccount(paymentAccountId).sumUpProperColumns()
        correctionLinkingPersistence.saveTotalsWhenFinishingPaymentAccount(paymentAccountId, totals = selectedPaymentTotals)

        return paymentAccountPersistence.finalizePaymentAccount(paymentAccountId).also {
            auditPublisher.publishEvent(paymentAccountsFinished(context = this, paymentAccount))
        }
    }

    private fun validateAccountIsDraft(paymentAccount: PaymentAccount) {
        if (paymentAccount.status != PaymentAccountStatus.DRAFT) {
            throw PaymentAccountNotInDraftException()
        }
    }
    private fun validateNoEcPaymentForAccountingYearIsDraft(paymentAccount: PaymentAccount) {
        val draftPaymentsForAccountingYear =
            ecPaymentPersistence.getDraftIdsByFundAndAccountingYear(paymentAccount.fund.id, paymentAccount.accountingYear.id)

        if (draftPaymentsForAccountingYear.isNotEmpty()) {
            throw EcPaymentsForAccountingYearStillInDraftException(
                mapOf(
                    Pair(
                        "ecPaymentId",
                        draftPaymentsForAccountingYear.joinToString(", ")
                    )
                )
            )
        }
    }

}
