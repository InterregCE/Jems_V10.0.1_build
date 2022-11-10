package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.dto.AdvancePaymentDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentUpdateDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.deleteAdvancePayment.DeleteAdvancePaymentInteractor
import io.cloudflight.jems.server.payments.service.getAdvancePaymentDetail.GetAdvancePaymentDetailInteractor
import io.cloudflight.jems.server.payments.service.getAdvancePayments.GetAdvancePaymentsInteractor
import io.cloudflight.jems.server.payments.service.model.AdvancePayment
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.service.model.AdvancePaymentUpdate
import io.cloudflight.jems.server.payments.service.updateAdvancePaymentDetail.UpdateAdvancePaymentDetailInteractor
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

class PaymentAdvanceControllerTest : UnitTest() {

    companion object {
        private val currentDate = ZonedDateTime.now().toLocalDate()
        private const val paymentId = 1L
        private const val projectId = 2L
        private const val partnerId = 3L
        private const val userId = 4L

        private val fundDTO = ProgrammeFundDTO(id = 5L, selected = true)
        private val fund = ProgrammeFund(id = 5L, selected = true)

        private val advancePaymentDTO = AdvancePaymentDTO(
            id = paymentId,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
            partnerNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fundDTO,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate,
            amountSettled = BigDecimal.ONE
        )
        private val advancePayment = AdvancePayment(
            id = paymentId,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
            partnerNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fund,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate,
            amountSettled = BigDecimal.ONE
        )
        private val advancePaymentDetail = AdvancePaymentDetail(
            id = paymentId,
            projectId = projectId,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            partnerId = partnerId,
            partnerType = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fund,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentAuthorizedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentConfirmedDate = currentDate.minusDays(2)
        )
        private val advancePaymentUpdate = AdvancePaymentUpdate(
            id = paymentId,
            projectId = projectId,
            partnerId = partnerId,
            programmeFundId = fund.id,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentConfirmed = true,
        )
        private val advancePaymentDetailDTO = AdvancePaymentDetailDTO(
            id = paymentId,
            projectId = projectId,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            partnerId = partnerId,
            partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
            partnerNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fundDTO,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentAuthorizedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentConfirmedDate = currentDate.minusDays(2)
        )

    }

    @MockK
    lateinit var getAdvancePayments: GetAdvancePaymentsInteractor
    @MockK
    lateinit var getAdvancePaymentDetail: GetAdvancePaymentDetailInteractor
    @MockK
    lateinit var updateAdvancePayment: UpdateAdvancePaymentDetailInteractor
    @MockK
    lateinit var deleteAdvancePayment: DeleteAdvancePaymentInteractor

    @InjectMockKs
    private lateinit var controller: PaymentAdvanceController

    @Test
    fun getAdvancePayments() {
        every { getAdvancePayments.list(any()) } returns PageImpl(listOf(advancePayment))

        assertThat(controller.getAdvancePayments(Pageable.unpaged())).containsExactly(
            advancePaymentDTO
        )
    }

    @Test
    fun getAdvancePaymentDetail() {
        every { getAdvancePaymentDetail.getPaymentDetail(paymentId) } returns advancePaymentDetail

        assertThat(controller.getAdvancePaymentDetail(paymentId)).isEqualTo(advancePaymentDetailDTO)
    }

    @Test
    fun deleteAdvancePayment() {
        every { deleteAdvancePayment.delete(paymentId) } returns Unit

        assertDoesNotThrow { controller.deleteAdvancePayment(paymentId) }
    }

    @Test
    fun updateAdvancePayment() {
        val updateDto  = AdvancePaymentUpdateDTO(
            id = paymentId,
            projectId = projectId,
            partnerId = partnerId,
            programmeFundId = fund.id,
            amountAdvance = BigDecimal.TEN,
            dateOfPayment = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentConfirmed = true
        )
        every {
            updateAdvancePayment.updateDetail(advancePaymentUpdate)
        } returns advancePaymentDetail

        assertThat(
            controller.updateAdvancePayment(updateDto)
        ).isEqualTo(advancePaymentDetailDTO)
    }

}
