package io.cloudflight.jems.server.payments.repository.accountingYears

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearPersistenceProvider
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AccountingYearPersistenceProviderTest: UnitTest() {
    companion object {
        private val startDate = LocalDate.now()
        private val endDate = LocalDate.now().plusDays(5)

        private val accountingYearsEntities = listOf(
            AccountingYearEntity(id = 1L, year = 2021, startDate = startDate, endDate = endDate)
        )

        private val accountingYears = listOf(
            AccountingYear(id = 1L, year = 2021, startDate = startDate, endDate = endDate)
        )
    }

    @MockK
    lateinit var repository: AccountingYearRepository

    @InjectMockKs
    lateinit var accountingYearPersistenceProvider: AccountingYearPersistenceProvider

    @Test
    fun getAccountingYears() {
        every { repository.findAllByOrderByYear() } returns accountingYearsEntities

        Assertions.assertThat(accountingYearPersistenceProvider.findAll()).isEqualTo(accountingYears)
    }
}
