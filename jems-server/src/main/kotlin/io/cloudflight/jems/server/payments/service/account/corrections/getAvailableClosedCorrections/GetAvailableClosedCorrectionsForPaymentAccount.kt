package io.cloudflight.jems.server.payments.service.account.corrections.getAvailableClosedCorrections

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

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableClosedCorrectionsForPaymentAccountException::class)
    override fun getClosedCorrections(pageable: Pageable, paymentAccountId: Long): Page<PaymentAccountCorrectionLinking> {
        val paymentAccount = paymentAccountPersistence.getByPaymentAccountId(paymentAccountId)

        val filter = if (paymentAccount.status.isFinished())
            constructCorrectionFilter(setOf(paymentAccount.id))
        else
            constructCorrectionFilter(paymentAccountIds = setOf(null, paymentAccount.id), fundId = paymentAccount.fund.id)

        return correctionPersistence.getCorrectionsLinkedToPaymentAccount(pageable, filter)
    }

    private fun constructCorrectionFilter(
        paymentAccountIds: Set<Long?>,
        fundId: Long? = null
    ) = PaymentAccountCorrectionSearchRequest(
        correctionStatus = AuditControlStatus.Closed,
        paymentAccountIds = paymentAccountIds,
        fundIds = if (fundId != null) setOf(fundId) else emptySet(),
        scenarios = listOf(
            ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
            ProjectCorrectionProgrammeMeasureScenario.SCENARIO_4
        )
    )
}
