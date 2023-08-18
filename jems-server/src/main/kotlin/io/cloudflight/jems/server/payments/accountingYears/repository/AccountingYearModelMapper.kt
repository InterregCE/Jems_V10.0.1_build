package io.cloudflight.jems.server.payments.accountingYears.repository

import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(AccountingYearModelMapper::class.java)

fun AccountingYear.toEntity(): AccountingYearEntity = mapper.map(this)

fun List<AccountingYearEntity>.toModel() = map { it.toModel() }
fun AccountingYearEntity.toModel(): AccountingYear = mapper.map(this)

@Mapper
interface AccountingYearModelMapper {
    fun map(entity: AccountingYearEntity): AccountingYear
    fun map(model: AccountingYear): AccountingYearEntity
}
