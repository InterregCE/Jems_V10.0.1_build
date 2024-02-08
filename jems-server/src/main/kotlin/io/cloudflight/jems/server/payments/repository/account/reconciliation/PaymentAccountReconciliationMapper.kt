package io.cloudflight.jems.server.payments.repository.account.reconciliation

import io.cloudflight.jems.server.payments.entity.account.PaymentAccountReconciliationEntity
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.PaymentAccountReconciliation
import io.cloudflight.jems.server.payments.repository.account.toModel


fun List<PaymentAccountReconciliationEntity>.toModel() = map { it.toModel() }
fun PaymentAccountReconciliationEntity.toModel() = PaymentAccountReconciliation(
    id = id,
    paymentAccount = paymentAccount.toModel(),
    priorityAxisId = priorityAxis.id,
    totalComment = totalComment,
    aaComment = aaComment,
    ecComment = ecComment
)
