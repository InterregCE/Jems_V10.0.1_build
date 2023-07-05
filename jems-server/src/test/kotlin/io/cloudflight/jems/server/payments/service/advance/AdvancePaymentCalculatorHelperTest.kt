package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.toScaledBigDecimal
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class AdvancePaymentCalculatorHelperTest: UnitTest() {

    companion object {

        val currentDate = LocalDate.now()
        private val fund = ProgrammeFund(id = 5L, selected = true)

        private val advancePayment = AdvancePayment(
            id = 1L,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            partnerType = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fund,
            amountPaid = 120.87.toScaledBigDecimal(),
            paymentDate = currentDate,
            amountSettled = BigDecimal.ZERO,
            paymentSettlements = listOf(
                AdvancePaymentSettlement(
                    id = 1L,
                    number = 1,
                    amountSettled = 1.34.toScaledBigDecimal(),
                    settlementDate = currentDate.minusDays(1),
                    comment = "half"
                ),
                AdvancePaymentSettlement(
                    id = 1L,
                    number = 1,
                    amountSettled = 2.40.toScaledBigDecimal(),
                    settlementDate = currentDate.minusDays(1),
                    comment = "half"
                )
            )
        )
    }


    @Test
    fun `sum of settled amounts`() {
        Assertions.assertEquals(advancePayment.calculateAmountSettled(), advancePayment.copy(amountSettled = 3.74.toScaledBigDecimal()))
    }

}