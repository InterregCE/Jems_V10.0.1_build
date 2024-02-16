package io.cloudflight.jems.server.payments.repository.account.finance

import io.cloudflight.jems.server.payments.entity.PaymentToEcPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLine
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(PaymentAccountFinanceModelMapper::class.java)

fun List<PaymentToEcPriorityAxisOverviewEntity>.toModel() =
    associate { Pair(it.priorityAxis?.id, mapper.map(it)) }

@Mapper
interface PaymentAccountFinanceModelMapper {

    @Mappings(
        Mapping(source = "priorityAxis.code", target = "priorityAxis"),
    )
    fun map(entity: PaymentToEcPriorityAxisOverviewEntity): PaymentAccountAmountSummaryLine

}
