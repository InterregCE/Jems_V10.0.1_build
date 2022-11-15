package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.payments.service.advance.getAdvancePayments.GetAdvancePayments
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

class GetAdvancePaymentsTest: UnitTest() {

    companion object {
        private val currentDate = ZonedDateTime.now().toLocalDate()
        private const val paymentId = 2L
        private const val fundId = 4L

        private val fund = ProgrammeFund(fundId, true)

        private val advancePayment = AdvancePayment(
            id = paymentId,
            projectCustomIdentifier = "identifier",
            projectAcronym = "acronym",
            partnerType = ProjectPartnerRoleDTO.PARTNER,
            partnerNumber =  2,
            partnerAbbreviation = "partner",
            programmeFund = fund,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate.minusDays(3),
            amountSettled = BigDecimal(100)
        )
    }

    @MockK
    lateinit var paymentPersistence: PaymentAdvancePersistence

    @InjectMockKs
    lateinit var getAdvancePayments: GetAdvancePayments


    @Test
    fun `list advance payments`() {
        every { paymentPersistence.list(Pageable.unpaged()) } returns PageImpl(listOf(advancePayment))

        assertThat(getAdvancePayments.list(Pageable.unpaged())).contains(advancePayment)
    }

}
