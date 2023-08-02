package io.cloudflight.jems.server.accountingYears.repository

import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountingYearRepository : JpaRepository<AccountingYearEntity, Long> {

    override fun findAll(): List<AccountingYearEntity>

}
