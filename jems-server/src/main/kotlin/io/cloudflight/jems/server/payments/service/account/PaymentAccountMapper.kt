package io.cloudflight.jems.server.payments.service.account

import io.cloudflight.jems.api.payments.dto.account.PaymentAccountDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountOverviewDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountStatusDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountUpdateDTO
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.PaymentAccountUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(PaymentAccountMapper::class.java)

fun PaymentAccount.toDto() = mapper.map(this)
fun PaymentAccountUpdate.toDto() = mapper.map(this)
fun List<PaymentAccountOverview>.toDto() = this.map { mapper.map(it) }

fun PaymentAccountDTO.toModel() = mapper.map(this)
fun PaymentAccountUpdateDTO.toModel() = mapper.map(this)

fun PaymentAccountStatus.toDto() = PaymentAccountStatusDTO.valueOf(this.toString())

@Mapper
interface PaymentAccountMapper {
    fun map(dto: PaymentAccountDTO): PaymentAccount
    fun map(dto: PaymentAccountUpdateDTO): PaymentAccountUpdate
    fun map(dto: PaymentAccountOverviewDTO): PaymentAccountOverview

    fun map(model: PaymentAccount): PaymentAccountDTO
    fun map(model: PaymentAccountUpdate): PaymentAccountUpdateDTO
    fun map(model: PaymentAccountOverview): PaymentAccountOverviewDTO
}
