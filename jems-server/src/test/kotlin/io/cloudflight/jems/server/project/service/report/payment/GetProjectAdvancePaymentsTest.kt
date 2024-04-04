package io.cloudflight.jems.server.project.service.report.payment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.overview.getProjectAdvancePayments.GetProjectAdvancePayments
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

class GetProjectAdvancePaymentsTest: UnitTest() {


    companion object {

        val currentDate = LocalDate.now()
        private val fund = ProgrammeFund(id = 5L, selected = true)

        private val advancePayment = AdvancePayment(
            id = 1L,
            partnerType = ProjectPartnerRole.LEAD_PARTNER,
            partnerSortNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fund,
            amountPaid = 120.87.toScaledBigDecimal(),
            paymentDate = currentDate,
            amountSettled = BigDecimal.ZERO,
            paymentSettlements = listOf(
                AdvancePaymentSettlement(
                    id = 1L,
                    number = 1,
                    amountSettled = BigDecimal(57.9),
                    settlementDate = currentDate.minusDays(1),
                    comment = "half"
                ),
                AdvancePaymentSettlement(
                    id = 1L,
                    number = 1,
                    amountSettled = BigDecimal(42.1),
                    settlementDate = currentDate.minusDays(1),
                    comment = "half"
                )
            ),
            projectAcronym = "project",
            projectCustomIdentifier = "identifier",
            partnerNameInOriginalLanguage = "name org lang",
            partnerNameInEnglish = "name en",
            projectId = 10L,
            linkedProjectVersion = "v1.0"
        )
    }

    @MockK
    private lateinit var paymentPersistence: PaymentAdvancePersistence

    @InjectMockKs
    private lateinit var getAdvancePayments: GetProjectAdvancePayments


    @Test
    fun `list advance payments`() {

        every { paymentPersistence.getConfirmedPaymentsForProject(1, Pageable.unpaged()) } returns PageImpl(listOf(advancePayment))

        val result = advancePayment.copy(amountSettled = 100.00.toScaledBigDecimal())
        assertThat(getAdvancePayments.list(1, Pageable.unpaged())).contains(result)
    }

}
