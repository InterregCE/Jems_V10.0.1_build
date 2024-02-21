package io.cloudflight.jems.server.payments.repository.account.reconciliation

import io.cloudflight.jems.server.payments.entity.account.PaymentAccountReconciliationEntity
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.PaymentAccountReconciliation
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.PaymentAccountReconciliationType
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountUpdate
import io.cloudflight.jems.server.payments.repository.account.PaymentAccountRepository
import io.cloudflight.jems.server.payments.service.account.reconciliation.PaymentAccountReconciliationPersistence
import io.cloudflight.jems.server.programme.repository.priority.ProgrammePriorityRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PaymentAccountReconciliationPersistenceProvider(
    private val accountReconciliationRepository: PaymentAccountReconciliationRepository,
    private val paymentAccountRepository: PaymentAccountRepository,
    private val programmePriorityRepository: ProgrammePriorityRepository
) : PaymentAccountReconciliationPersistence {

    @Transactional(readOnly = true)
    override fun getByPaymentAccountId(
        paymentAccountId: Long,
    ): List<PaymentAccountReconciliation> =
        accountReconciliationRepository.getByPaymentAccountId(paymentAccountId).toModel()

    @Transactional
    override fun updateReconciliation(
        paymentAccountId: Long,
        reconciliationUpdate: ReconciledAmountUpdate
    ): PaymentAccountReconciliation {
        val paymentReconciliation = accountReconciliationRepository
            .getByPaymentAccountIdAndPriorityAxisId(paymentAccountId, reconciliationUpdate.priorityAxisId)
            .orElseGet { getNewEntity(priorityId = reconciliationUpdate.priorityAxisId, paymentAccountId) }

        paymentReconciliation.updateWith(reconciliationUpdate)

        if (paymentReconciliation.isEmpty())
            accountReconciliationRepository.delete(paymentReconciliation)

        return paymentReconciliation.toModel()

    }

    private fun getNewEntity(priorityId: Long, paymentAccountId: Long): PaymentAccountReconciliationEntity {
        val paymentAccount = paymentAccountRepository.getById(paymentAccountId)
        val programmePriority = programmePriorityRepository.getById(priorityId)

        return accountReconciliationRepository.save(
            PaymentAccountReconciliationEntity(0L, paymentAccount, programmePriority, "", "", "")
        )
    }

    private fun PaymentAccountReconciliationEntity.isEmpty() =
        totalComment.isEmpty() && aaComment.isEmpty() && ecComment.isEmpty()

    private fun PaymentAccountReconciliationEntity.updateWith(reconciliationUpdate: ReconciledAmountUpdate): PaymentAccountReconciliationEntity {
        when (reconciliationUpdate.type) {
            PaymentAccountReconciliationType.Total -> totalComment = reconciliationUpdate.comment
            PaymentAccountReconciliationType.OfAa -> aaComment = reconciliationUpdate.comment
            PaymentAccountReconciliationType.OfEc -> ecComment = reconciliationUpdate.comment
        }

        return this
    }

}
