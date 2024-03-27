package io.cloudflight.jems.server.payments.service.account.finance.reconciliation.getReconciliationOverview

import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinking
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountByType
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledScenario
import io.cloudflight.jems.server.payments.service.account.finance.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.payments.service.account.reconciliation.PaymentAccountReconciliationPersistence
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PaymentAccountReconciliationOverviewService(
    private val reconciliationPersistence: PaymentAccountReconciliationPersistence,
    private val paymentAccountFinancePersistence: PaymentAccountFinancePersistence,
    private val programmePriorityPersistence: ProgrammePriorityPersistence
) {

    @Transactional(readOnly = true)
    fun getReconciliationOverview(paymentAccountId: Long): List<ReconciledAmountPerPriority> {

        val scenario4NoClericalMistake =
            paymentAccountFinancePersistence.getReconciliationOverview(paymentAccountId, ReconciledScenario.Scenario4)
                .associateBy { it.priorityId }
        val scenario3NoClericalMistake =
            paymentAccountFinancePersistence.getReconciliationOverview(paymentAccountId, ReconciledScenario.Scenario3)
                .associateBy { it.priorityId }
        val clericalMistake = paymentAccountFinancePersistence.getReconciliationOverview(
            paymentAccountId,
            ReconciledScenario.ClericalMistakes
        )
            .associateBy { it.priorityId }

        val allPriorities = programmePriorityPersistence.getAllMax56Priorities()
        val reconciliationList =
            reconciliationPersistence.getByPaymentAccountId(paymentAccountId).associateBy { it.priorityAxisId }

        return allPriorities.map { (id, code) ->
            ReconciledAmountPerPriority(
                priorityAxis = code,
                reconciledAmountTotal = ReconciledAmountByType(
                    scenario4Sum = scenario4NoClericalMistake[id]?.total ?: BigDecimal.ZERO,
                    scenario3Sum = scenario3NoClericalMistake[id]?.total ?: BigDecimal.ZERO,
                    clericalMistakesSum = clericalMistake[id]?.total ?: BigDecimal.ZERO,
                    comment = reconciliationList[id]?.totalComment ?: "",
                ),
                reconciledAmountOfAa = ReconciledAmountByType(
                    scenario4Sum = scenario4NoClericalMistake[id]?.ofWhichAa ?: BigDecimal.ZERO,
                    scenario3Sum = scenario3NoClericalMistake[id]?.ofWhichAa ?: BigDecimal.ZERO,
                    clericalMistakesSum = clericalMistake[id]?.ofWhichAa ?: BigDecimal.ZERO,
                    comment = reconciliationList[id]?.aaComment ?: "",
                ),
                reconciledAmountOfEc = ReconciledAmountByType(
                    scenario4Sum = scenario4NoClericalMistake[id]?.ofWhichEc ?: BigDecimal.ZERO,
                    scenario3Sum = scenario3NoClericalMistake[id]?.ofWhichEc ?: BigDecimal.ZERO,
                    clericalMistakesSum = clericalMistake[id]?.ofWhichEc ?: BigDecimal.ZERO,
                    comment = reconciliationList[id]?.ecComment ?: "",
                ),
            )
        }
    }

    fun List<PaymentAccountCorrectionLinking>.total() = sumOf { it.fundAmount.add(it.partnerContribution) }
}
