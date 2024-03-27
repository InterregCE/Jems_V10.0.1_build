package io.cloudflight.jems.server.payments.service.account.finance.withdrawn.getWithdrawnOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerYear
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.CorrectionAmountWithdrawn
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class GetWithdrawnOverviewTest: UnitTest() {

    companion object {

        private val yearFound5 = AccountingYear(
            15L, 5, LocalDate.of(2025, 4, 20), LocalDate.of(2026, 4, 30),
        )
        private val yearFound6 = AccountingYear(
            16L, 6, LocalDate.of(2026, 3, 28), LocalDate.of(2027, 5, 1),
        )

        private val po1 = AmountWithdrawnPerPriority(
            priorityAxis = "PO.1",
            perYear = listOf(
                AmountWithdrawnPerYear(
                    year = yearFound5,
                    withdrawalTotal = BigDecimal.valueOf(130L),
                    withdrawalPublic = BigDecimal.valueOf(90L),
                    withdrawalTotalOfWhichAa = BigDecimal.valueOf(60L),
                    withdrawalPublicOfWhichAa = BigDecimal.valueOf(40L),
                    withdrawalTotalOfWhichEc = BigDecimal.valueOf(70L),
                    withdrawalPublicOfWhichEc = BigDecimal.valueOf(50L),
                ),
                AmountWithdrawnPerYear(
                    year = yearFound6,
                    withdrawalTotal = BigDecimal.valueOf(80L),
                    withdrawalPublic = BigDecimal.valueOf(60L),
                    withdrawalTotalOfWhichAa = BigDecimal.valueOf(0L),
                    withdrawalPublicOfWhichAa = BigDecimal.valueOf(0L),
                    withdrawalTotalOfWhichEc = BigDecimal.valueOf(0L),
                    withdrawalPublicOfWhichEc = BigDecimal.valueOf(0L),
                ),
            ),
            withdrawalTotal = BigDecimal.valueOf(210L),
            withdrawalPublic = BigDecimal.valueOf(150L),
        )

        private val po2 = AmountWithdrawnPerPriority(
            priorityAxis = "PO.2",
            perYear = listOf(
                AmountWithdrawnPerYear(
                    year = yearFound5,
                    withdrawalTotal = BigDecimal.valueOf(90L),
                    withdrawalPublic = BigDecimal.valueOf(70L),
                    withdrawalTotalOfWhichAa = BigDecimal.valueOf(0L),
                    withdrawalPublicOfWhichAa = BigDecimal.valueOf(0L),
                    withdrawalTotalOfWhichEc = BigDecimal.valueOf(90L),
                    withdrawalPublicOfWhichEc = BigDecimal.valueOf(70L),
                ),
            ),
            withdrawalTotal = BigDecimal.valueOf(90L),
            withdrawalPublic = BigDecimal.valueOf(70L),
        )

    }

    @MockK private lateinit var paymentsAccountWithdrawnOverviewService: PaymentsAccountWithdrawnOverviewService

    @InjectMockKs
    private lateinit var service: GetWithdrawnOverview

    @Test
    fun getWithdrawnOverview() {


        every {
            paymentsAccountWithdrawnOverviewService.getWithdrawnOverview(98L)
        } returns listOf(po1, po2,)

        assertThat(service.getWithdrawnOverview(98L)).containsExactly(
            AmountWithdrawnPerPriority(
                priorityAxis = "PO.1",
                perYear = listOf(
                    AmountWithdrawnPerYear(
                        year = yearFound5,
                        withdrawalTotal = BigDecimal.valueOf(130L),
                        withdrawalPublic = BigDecimal.valueOf(90L),
                        withdrawalTotalOfWhichAa = BigDecimal.valueOf(60L),
                        withdrawalPublicOfWhichAa = BigDecimal.valueOf(40L),
                        withdrawalTotalOfWhichEc = BigDecimal.valueOf(70L),
                        withdrawalPublicOfWhichEc = BigDecimal.valueOf(50L),
                    ),
                    AmountWithdrawnPerYear(
                        year = yearFound6,
                        withdrawalTotal = BigDecimal.valueOf(80L),
                        withdrawalPublic = BigDecimal.valueOf(60L),
                        withdrawalTotalOfWhichAa = BigDecimal.valueOf(0L),
                        withdrawalPublicOfWhichAa = BigDecimal.valueOf(0L),
                        withdrawalTotalOfWhichEc = BigDecimal.valueOf(0L),
                        withdrawalPublicOfWhichEc = BigDecimal.valueOf(0L),
                    ),
                ),
                withdrawalTotal = BigDecimal.valueOf(210L),
                withdrawalPublic = BigDecimal.valueOf(150L),
            ),
            AmountWithdrawnPerPriority(
                priorityAxis = "PO.2",
                perYear = listOf(
                    AmountWithdrawnPerYear(
                        year = yearFound5,
                        withdrawalTotal = BigDecimal.valueOf(90L),
                        withdrawalPublic = BigDecimal.valueOf(70L),
                        withdrawalTotalOfWhichAa = BigDecimal.valueOf(0L),
                        withdrawalPublicOfWhichAa = BigDecimal.valueOf(0L),
                        withdrawalTotalOfWhichEc = BigDecimal.valueOf(90L),
                        withdrawalPublicOfWhichEc = BigDecimal.valueOf(70L),
                    ),
                ),
                withdrawalTotal = BigDecimal.valueOf(90L),
                withdrawalPublic = BigDecimal.valueOf(70L),
            )

        )
    }

}
