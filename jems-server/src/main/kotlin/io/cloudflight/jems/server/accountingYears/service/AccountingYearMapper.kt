package io.cloudflight.jems.server.accountingYears.service

import io.cloudflight.jems.api.accountingYear.AccountingYearDTO
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(AccountingYearMapper::class.java)

fun AccountingYearDTO.toModel(): AccountingYear = mapper.map(this)
fun List<AccountingYear>.toDto() = map { it.toDto() }
fun AccountingYear.toDto() = mapper.map(this)

@Mapper
interface AccountingYearMapper {
    fun map(dto: AccountingYearDTO): AccountingYear
    fun map(model: AccountingYear): AccountingYearDTO
}


