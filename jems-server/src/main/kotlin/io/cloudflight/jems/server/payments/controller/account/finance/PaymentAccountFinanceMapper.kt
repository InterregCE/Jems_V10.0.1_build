package io.cloudflight.jems.server.payments.controller.account.finance

import io.cloudflight.jems.api.payments.dto.account.finance.reconciliation.ReconciledAmountPerPriorityDTO
import io.cloudflight.jems.api.payments.dto.account.finance.reconciliation.ReconciledAmountUpdateDTO
import io.cloudflight.jems.api.payments.dto.account.finance.withdrawn.AmountWithdrawnPerPriorityDTO
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountUpdate
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerPriority
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(PaymentAccountFinanceMapper::class.java)

fun List<AmountWithdrawnPerPriority>.toDto() = map { mapper.map(it) }

fun List<ReconciledAmountPerPriority>.toAmountReconciledDto() = map { mapper.map(it) }
fun ReconciledAmountUpdateDTO.toModel() = mapper.map(this)

@Mapper
interface PaymentAccountFinanceMapper {
    fun map(model: AmountWithdrawnPerPriority): AmountWithdrawnPerPriorityDTO
    fun map(model: ReconciledAmountPerPriority): ReconciledAmountPerPriorityDTO
    fun map(dto: ReconciledAmountUpdateDTO): ReconciledAmountUpdate
}
