package io.cloudflight.jems.server.project.controller.report.payment

import io.cloudflight.jems.api.payments.dto.AdvancePaymentDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.payment.getProjectAdvancePayments.GetProjectAdvancePaymentsInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import java.math.BigDecimal
import java.time.LocalDate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ProjectAdvancedPaymentsControllerTest : UnitTest() {
    companion object {
        private val currentDate = LocalDate.now()
        private const val paymentId = 1L

        private val fundDTO = ProgrammeFundDTO(id = 5L, selected = true)
        private val fund = ProgrammeFund(id = 5L, selected = true)

        private val advancePaymentDTO = AdvancePaymentDTO(
            id = paymentId,
            partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
            partnerSortNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fundDTO,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            amountSettled = BigDecimal.ONE,
            projectAcronym = "project",
            projectCustomIdentifier = "identifier"
        )
        private val advancePayment = AdvancePayment(
            id = paymentId,
            partnerType = ProjectPartnerRole.LEAD_PARTNER,
            partnerSortNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fund,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            amountSettled = BigDecimal.ONE,
            paymentSettlements = emptyList(),
            projectAcronym = "project",
            projectCustomIdentifier = "identifier"
        )
    }

    @MockK
    lateinit var getAdvancePayments: GetProjectAdvancePaymentsInteractor

    @InjectMockKs
    private lateinit var controller: ProjectAdvancedPaymentsController

    @Test
    fun getAdvancePayments() {
        every { getAdvancePayments.list(any(), any()) } returns PageImpl(listOf(
            advancePayment
        ))

        Assertions.assertThat(controller.getAdvancePayments(472, Pageable.unpaged()))
            .containsExactly(advancePaymentDTO)
    }
}