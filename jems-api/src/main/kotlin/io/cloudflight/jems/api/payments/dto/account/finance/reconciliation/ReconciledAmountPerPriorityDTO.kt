package io.cloudflight.jems.api.payments.dto.account.finance.reconciliation

data class ReconciledAmountPerPriorityDTO(
    val priorityAxis: String,

    val reconciledAmountTotal: ReconciledAmountByTypeDTO,
    val reconciledAmountOfAa: ReconciledAmountByTypeDTO,
    val reconciledAmountOfEc: ReconciledAmountByTypeDTO,
)
