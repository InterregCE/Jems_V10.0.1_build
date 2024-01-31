package io.cloudflight.jems.server.payments.controller.account.finance

import io.cloudflight.jems.api.payments.dto.account.finance.withdrawn.AmountWithdrawnPerPriorityDTO
import io.cloudflight.jems.api.payments.dto.account.finance.withdrawn.AmountWithdrawnPerYearDTO
import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerYear
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.service.account.finance.withdrawn.getWithdrawnOverview.GetWithdrawnOverviewInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class PaymentAccountWithdrawnControllerTest : UnitTest() {

    companion object {
        private val yearStart = LocalDate.of(2024, 4 , 18)
        private val yearEnd = LocalDate.of(2025, 5 , 30)

        private val dataExample = AmountWithdrawnPerPriority(
            priorityAxis = "PO-1",
            perYear = listOf(
                AmountWithdrawnPerYear(
                    year = AccountingYear(68L, 4, yearStart, yearEnd),
                    withdrawalTotal = BigDecimal.valueOf(590L),
                    withdrawalPublic = BigDecimal.valueOf(470L),
                    withdrawalTotalOfWhichAa = BigDecimal.valueOf(350L),
                    withdrawalPublicOfWhichAa = BigDecimal.valueOf(250L),
                    withdrawalTotalOfWhichEc = BigDecimal.valueOf(110L),
                    withdrawalPublicOfWhichEc = BigDecimal.valueOf(60L),
                ),
            ),
            withdrawalTotal = BigDecimal.valueOf(590L),
            withdrawalPublic = BigDecimal.valueOf(470L),
        )

        private val expectedResult = AmountWithdrawnPerPriorityDTO(
            priorityAxis = "PO-1",
            perYear = listOf(
                AmountWithdrawnPerYearDTO(
                    year = AccountingYearDTO(68L, 4, yearStart, yearEnd),
                    withdrawalTotal = BigDecimal.valueOf(590L),
                    withdrawalPublic = BigDecimal.valueOf(470L),
                    withdrawalTotalOfWhichAa = BigDecimal.valueOf(350L),
                    withdrawalPublicOfWhichAa = BigDecimal.valueOf(250L),
                    withdrawalTotalOfWhichEc = BigDecimal.valueOf(110L),
                    withdrawalPublicOfWhichEc = BigDecimal.valueOf(60L),
                ),
            ),
            withdrawalTotal = BigDecimal.valueOf(590L),
            withdrawalPublic = BigDecimal.valueOf(470L),
        )

    }

    @MockK
    private lateinit var getWithdrawnOverview: GetWithdrawnOverviewInteractor

    @InjectMockKs
    private lateinit var controller: PaymentAccountWithdrawnController

    @Test
    fun getWithdrawnOverview() {
        every { getWithdrawnOverview.getWithdrawnOverview(48L) } returns
                listOf(dataExample)

        assertThat(controller.getWithdrawnOverview(48L))
            .containsExactly(expectedResult)
    }

}
