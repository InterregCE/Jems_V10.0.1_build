package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.accountingYear.AccountingYearDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.accountingYears.controller.AccountingYearController
import io.cloudflight.jems.server.payments.accountingYears.service.getAccountingYear.GetAccountingYearInteractor
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AccountingYearControllerTest: UnitTest() {

    companion object {
        private val startDate = LocalDate.now()
        private val endDate = LocalDate.now().plusDays(5)

        private val accountingYears = listOf(
            AccountingYear(id = 1L, year = 2021, startDate = startDate, endDate = endDate)
        )
        private val expectedAccountingYears = listOf(
            AccountingYearDTO(id = 1L, year = 2021, startDate = startDate, endDate = endDate)
        )
    }

    @MockK
    lateinit var getAccountingYears: GetAccountingYearInteractor

    @InjectMockKs
    lateinit var accountingYearController: AccountingYearController

    @Test
    fun getAccountingYears() {
        every { getAccountingYears.getAccountingYears() } returns accountingYears

        assertThat(accountingYearController.getAccountingYears()).isEqualTo(expectedAccountingYears)
    }
}
