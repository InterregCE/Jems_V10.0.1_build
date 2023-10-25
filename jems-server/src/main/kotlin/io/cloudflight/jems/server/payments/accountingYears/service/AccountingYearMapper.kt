package io.cloudflight.jems.server.payments.accountingYears.service

import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearAvailabilityDTO
import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.ec.AccountingYearAvailability
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(AccountingYearMapper::class.java)

fun AccountingYearDTO.toModel(): AccountingYear = mapper.map(this)
fun List<AccountingYear>.toDto() = map { it.toDto() }
fun AccountingYear.toDto() = mapper.map(this)
fun List<AccountingYearAvailability>.toAvailabilityDto() = map { mapper.map(it) }

@Mapper
interface AccountingYearMapper {
    fun map(dto: AccountingYearDTO): AccountingYear
    fun map(model: AccountingYear): AccountingYearDTO
    fun map(model: AccountingYearAvailability): AccountingYearAvailabilityDTO
}


