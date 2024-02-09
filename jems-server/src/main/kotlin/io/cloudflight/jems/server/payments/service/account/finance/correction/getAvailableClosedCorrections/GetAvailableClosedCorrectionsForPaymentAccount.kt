package io.cloudflight.jems.server.payments.service.account.finance.correction.getAvailableClosedCorrections

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionLinking
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionSearchRequest
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableClosedCorrectionsForPaymentAccount(
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val correctionPersistence: AuditControlCorrectionPersistence
) : GetAvailableClosedCorrectionsForPaymentAccountInteractor {

    companion object {
        private fun filterForAccountAvailableCorrections(
            accountIds: Set<Long?>,
            fundId: Long? = null,
        ) = PaymentAccountCorrectionSearchRequest(
            correctionStatus = AuditControlStatus.Closed,
            paymentAccountIds = accountIds,
            fundIds = if (fundId != null) setOf(fundId) else emptySet(),
            scenarios = ProjectCorrectionProgrammeMeasureScenario.linkableToPaymentAccount,
        )
    }

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableClosedCorrectionsForPaymentAccountException::class)
    override fun getClosedCorrections(pageable: Pageable, paymentAccountId: Long): Page<PaymentAccountCorrectionLinking> {
        val paymentAccount = paymentAccountPersistence.getByPaymentAccountId(paymentAccountId)
        val fundId = paymentAccount.fund.id

        val filter = if (paymentAccount.status.isFinished())
            filterForAccountAvailableCorrections(accountIds = paymentAccountId.asSet())
        else
            filterForAccountAvailableCorrections(accountIds = paymentAccountId.orNull(), fundId = fundId)

        return correctionPersistence.getCorrectionsLinkedToPaymentAccount(pageable, filter)
    }

    private fun Long.orNull() = setOf(this, null)
    private fun Long.asSet() = setOf(this)

}
