package io.cloudflight.jems.server.payments.service.accountingYear

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.accountingYears.service.AccountingYearPersistence
import io.cloudflight.jems.server.payments.accountingYears.service.getAccountingYear.GetAccountingYears
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class GetAccountingYearTest: UnitTest() {

    companion object {
        private val startDate = LocalDate.now()
        private val endDate = LocalDate.now().plusDays(5)

        private val accountingYears = listOf(
            AccountingYear(id = 1L, year = 2021, startDate = startDate, endDate = endDate)
        )
    }

    @MockK
    lateinit var persistence: AccountingYearPersistence

    @InjectMockKs
    lateinit var getAccountingYears: GetAccountingYears

    @Test
    fun getAccountingYears() {
        every { persistence.findAll() } returns accountingYears

        Assertions.assertThat(getAccountingYears.getAccountingYears()).isEqualTo(accountingYears)
    }
}
