package io.cloudflight.jems.server.payments.controller.account.finance

import io.cloudflight.jems.api.payments.dto.account.finance.withdrawn.AmountWithdrawnPerPriorityDTO
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerPriority
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(PaymentAccountFinanceMapper::class.java)

fun List<AmountWithdrawnPerPriority>.toDto() = map { mapper.map(it) }

@Mapper
interface PaymentAccountFinanceMapper {
    fun map(model: AmountWithdrawnPerPriority): AmountWithdrawnPerPriorityDTO
}
