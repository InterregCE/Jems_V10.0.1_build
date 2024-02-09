package io.cloudflight.jems.server.payments.service.account.finance.withdrawn.getWithdrawnOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerYear
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.CorrectionAmountWithdrawn
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.service.account.finance.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class GetWithdrawnOverviewTest: UnitTest() {

    companion object {
        private val yearIncluded = AccountingYear(
            14L, 4, LocalDate.of(2024, 4, 18), LocalDate.of(2025, 5, 30),
        )
        private val yearFound5 = AccountingYear(
            15L, 5, LocalDate.of(2025, 4, 20), LocalDate.of(2026, 4, 30),
        )
        private val yearFound6 = AccountingYear(
            16L, 6, LocalDate.of(2026, 3, 28), LocalDate.of(2027, 5, 1),
        )

        private val correctionAA = CorrectionAmountWithdrawn(
            id = 21L,
            controllingBody = ControllingBody.AA,
            priorityAxis = "PO.1",
            public = BigDecimal.valueOf(40L),
            total = BigDecimal.valueOf(60L),
            ecPaymentIdWhenFound = 8L,
            yearWhenFound = yearFound5,
            ecPaymentIdWhenIncluded = 10L,
            yearWhenIncluded = yearIncluded,
        )

        private val correctionEC = CorrectionAmountWithdrawn(
            id = 22L,
            controllingBody = ControllingBody.EC,
            priorityAxis = "PO.1",
            public = BigDecimal.valueOf(50L),
            total = BigDecimal.valueOf(70L),
            ecPaymentIdWhenFound = 8L,
            yearWhenFound = yearFound5,
            ecPaymentIdWhenIncluded = 11L,
            yearWhenIncluded = yearIncluded,
        )

        private val correctionGoA = CorrectionAmountWithdrawn(
            id = 23L,
            controllingBody = ControllingBody.GoA,
            priorityAxis = "PO.1",
            public = BigDecimal.valueOf(60L),
            total = BigDecimal.valueOf(80L),
            ecPaymentIdWhenFound = 9L,
            yearWhenFound = yearFound6,
            ecPaymentIdWhenIncluded = 11L,
            yearWhenIncluded = yearIncluded,
        )

        private val correctionECA = CorrectionAmountWithdrawn(
            id = 24L,
            controllingBody = ControllingBody.ECA,
            priorityAxis = "PO.2",
            public = BigDecimal.valueOf(70L),
            total = BigDecimal.valueOf(90L),
            ecPaymentIdWhenFound = 8L,
            yearWhenFound = yearFound5,
            ecPaymentIdWhenIncluded = 11L,
            yearWhenIncluded = yearIncluded,
        )

        private val expectedPo1 = AmountWithdrawnPerPriority(
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

        private val expectedPo2 = AmountWithdrawnPerPriority(
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

    @MockK private lateinit var persistence: PaymentAccountPersistence
    @MockK private lateinit var financePersistence: PaymentAccountFinancePersistence

    @InjectMockKs
    private lateinit var service: GetWithdrawnOverview

    @Test
    fun getWithdrawnOverview() {
        every { persistence.getByPaymentAccountId(98L) } returns mockk {
            every { fund.id } returns 74L
            every { accountingYear.id } returns 63L
        }

        every {
            financePersistence.getCorrectionsOnlyDeductionsAndNonClericalMistake(74L, 63L)
        } returns listOf(
            correctionAA,
            correctionEC,
            correctionGoA,
            correctionECA,
        )

        assertThat(service.getWithdrawnOverview(98L)).containsExactly(
            expectedPo1,
            expectedPo2,
        )
    }

}
