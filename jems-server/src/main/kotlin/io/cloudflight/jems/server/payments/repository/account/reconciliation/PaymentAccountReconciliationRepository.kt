package io.cloudflight.jems.server.payments.repository.account.reconciliation

import io.cloudflight.jems.server.payments.entity.account.PaymentAccountReconciliationEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PaymentAccountReconciliationRepository: JpaRepository<PaymentAccountReconciliationEntity, Long> {
    fun getByPaymentAccountId(paymentAccountId: Long): List<PaymentAccountReconciliationEntity>

    fun getByPaymentAccountIdAndPriorityAxisId(
        paymentAccountId: Long,
        priorityAxisId: Long,
    ): Optional<PaymentAccountReconciliationEntity>

}
