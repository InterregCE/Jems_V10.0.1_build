package io.cloudflight.jems.server.payments.controller


import io.cloudflight.jems.api.payments.dto.AdvancePaymentDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentSearchRequestDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentSettlementDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentUpdateDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import io.cloudflight.jems.server.payments.service.advance.deleteAdvancePayment.DeleteAdvancePaymentInteractor
import io.cloudflight.jems.server.payments.service.advance.getAdvancePaymentDetail.GetAdvancePaymentDetailInteractor
import io.cloudflight.jems.server.payments.service.advance.getAdvancePayments.GetAdvancePaymentsInteractor
import io.cloudflight.jems.server.payments.service.advance.updateAdvancePaymentDetail.UpdateAdvancePaymentDetailInteractor
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

class PaymentAdvanceControllerTest : UnitTest() {

    companion object {
        private val currentDate = LocalDate.now()
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
            partnerSortNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fundDTO,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            amountSettled = BigDecimal.ONE,
            partnerNameInOriginalLanguage = "name org lang",
            partnerNameInEnglish = "name en",
            linkedProjectVersion = "v1.0",
            lastApprovedProjectVersion = "v1.0",
        )
        private val advancePayment = AdvancePayment(
            id = paymentId,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            partnerType = ProjectPartnerRole.LEAD_PARTNER,
            partnerSortNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fund,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            amountSettled = BigDecimal.ONE,
            paymentSettlements = emptyList(),
            partnerNameInOriginalLanguage = "name org lang",
            partnerNameInEnglish = "name en",
            linkedProjectVersion = "v1.0",
            lastApprovedProjectVersion = "v1.0",
            projectId = projectId
        )

        private val advancePaymentSettlement = AdvancePaymentSettlement(
            id = 1L,
            number = 1,
            amountSettled = BigDecimal(5),
            settlementDate = currentDate.minusDays(1),
            comment = "half"
        )

        private val advancePaymentDetail = AdvancePaymentDetail(
            id = paymentId,
            projectId = projectId,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            projectVersion = "1.0",
            partnerId = partnerId,
            partnerType = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fund,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentAuthorizedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentConfirmedDate = currentDate.minusDays(2),
            paymentSettlements = listOf(advancePaymentSettlement),

        )
        private val advancePaymentUpdate = AdvancePaymentUpdate(
            id = paymentId,
            projectId = projectId,
            partnerId = partnerId,
            programmeFundId = fund.id,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentConfirmed = true,
            paymentSettlements = listOf(advancePaymentSettlement),
        )

        private val advancePaymentSettlementDTO = AdvancePaymentSettlementDTO(
            id = 1L,
            number = 1,
            amountSettled = BigDecimal(5),
            settlementDate = currentDate.minusDays(1),
            comment = "half"
        )

        private val advancePaymentDetailDTO = AdvancePaymentDetailDTO(
            id = paymentId,
            projectId = projectId,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            projectVersion = "1.0",
            partnerId = partnerId,
            partnerType = ProjectPartnerRoleDTO.LEAD_PARTNER,
            partnerNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fundDTO,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentAuthorizedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = true,
            paymentConfirmedUser = OutputUser(userId, "random@mail", "name", "surname"),
            paymentConfirmedDate = currentDate.minusDays(2),
            paymentSettlements = listOf(advancePaymentSettlementDTO)
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
        val slotFilter = slot<AdvancePaymentSearchRequest>()
        every { getAdvancePayments.list(any(), capture(slotFilter)) } returns PageImpl(listOf(advancePayment))

        val filter = AdvancePaymentSearchRequestDTO(
            paymentId = 855L,
            projectIdentifiers = setOf("472", "INT00473"),
            projectAcronym = "acr-filter",
            fundIds = setOf(511L, 512L),
            amountFrom = BigDecimal.ONE,
            amountTo = BigDecimal.TEN,
            dateFrom = currentDate.minusDays(1),
            dateTo = currentDate.plusDays(1),
            authorized = true,
            confirmed = false,
        )

        assertThat(controller.getAdvancePayments(Pageable.unpaged(), filter)).containsExactly(advancePaymentDTO)
        assertThat(slotFilter.captured).isEqualTo(
            AdvancePaymentSearchRequest(
                paymentId = 855L,
                projectIdentifiers = setOf("472", "INT00473"),
                projectAcronym = "acr-filter",
                fundIds = setOf(511L, 512L),
                amountFrom = BigDecimal.ONE,
                amountTo = BigDecimal.TEN,
                dateFrom = currentDate.minusDays(1),
                dateTo = currentDate.plusDays(1),
                authorized = true,
                confirmed = false,
            )
        )
    }

    @Test
    fun `getAdvancePayments - empty filter`() {
        val slotFilter = slot<AdvancePaymentSearchRequest>()
        every { getAdvancePayments.list(any(), capture(slotFilter)) } returns PageImpl(emptyList())

        assertThat(controller.getAdvancePayments(Pageable.unpaged(), AdvancePaymentSearchRequestDTO())).isEmpty()
        assertThat(slotFilter.captured).isEqualTo(
            AdvancePaymentSearchRequest(paymentId = null, projectIdentifiers = emptySet(), projectAcronym = null,
                fundIds = emptySet(), amountFrom = null, amountTo = null, dateFrom = null, dateTo = null,
                authorized = null, confirmed = null)
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
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentConfirmed = true,
            paymentSettlements = listOf(AdvancePaymentSettlementDTO(
                id = 1L,
                number = 1,
                amountSettled = BigDecimal(5),
                settlementDate = currentDate.minusDays(1),
                comment = "half"
            ))
        )
        every {
            updateAdvancePayment.updateDetail(advancePaymentUpdate)
        } returns advancePaymentDetail

        assertThat(
            controller.updateAdvancePayment(updateDto)
        ).isEqualTo(advancePaymentDetailDTO)
    }

}
