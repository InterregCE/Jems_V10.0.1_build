package io.cloudflight.jems.api.payments.dto.account.finance.reconciliation

data class ReconciledAmountUpdateDTO(
    val priorityAxisId: Long,
    val type: PaymentAccountReconciliationTypeDTO,
    val comment: String
)
