package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getAvailableAccountingYearsForPaymentFund

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Month

class GetAvailableAccountingYearsForPaymentFundTest: UnitTest() {

    companion object {


        private val accountingYears = listOf(
            AccountingYear(id = 1L, year = 2021,
                startDate = LocalDate.of(2021, Month.JANUARY, 1),
                endDate = LocalDate.of(2022, Month.JUNE, 30)
            ),
            AccountingYear(
                id = 2L, year = 2022,
                startDate = LocalDate.of(2022, Month.JANUARY, 7),
                endDate = LocalDate.of(2023, Month.JUNE, 30)
            )
        )
    }

    @MockK
    lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    lateinit var getAvailableAccountingYearsForPaymentFund: GetAvailableAccountingYearsForPaymentFund



    @Test
    fun getAvailableAccountingYearsForPaymentFund() {
        every { paymentApplicationsToEcPersistence.getAvailableAccountingYearsForFund(1L) } returns accountingYears
        assertThat(getAvailableAccountingYearsForPaymentFund.getAvailableAccountingYearsForPaymentFund(1L)).isEqualTo(
            listOf(
                AccountingYear(id = 1L, year = 2021,
                    startDate = LocalDate.of(2021, Month.JANUARY, 1),
                    endDate = LocalDate.of(2022, Month.JUNE, 30)
                ),
                AccountingYear(
                    id = 2L, year = 2022,
                    startDate = LocalDate.of(2022, Month.JANUARY, 7),
                    endDate = LocalDate.of(2023, Month.JUNE, 30)
                )
            )
        )
    }
}