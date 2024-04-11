package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import io.cloudflight.jems.server.payments.service.advance.updateAdvancePaymentDetail.UpdateAdvancePaymentDetail
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

class UpdateAdvancePaymentTest : UnitTest() {

    companion object {
        private val currentDate = ZonedDateTime.now().toLocalDate()
        private const val paymentId = 1L
        private const val partnerId = 2L
        private const val projectId = 3L
        private const val currentUserId = 7L
        private const val fundId = 4L
        private const val userId = 6L
        private const val version = "1.0"

        private val fund = ProgrammeFund(
            id = fundId,
            selected = true,
            abbreviation = setOf(InputTranslation(SystemLanguage.EN, "FUND")),
        )

        private val paymentSettlement = AdvancePaymentSettlement(
            id = 1L,
            number = 1,
            amountSettled = BigDecimal(5),
            settlementDate = currentDate.minusDays(1),
            comment = "half"
        )

        private val paymentUpdate = AdvancePaymentUpdate(
            id = paymentId,
            projectId = projectId,
            partnerId = partnerId,
            programmeFundId = fund.id,
            amountPaid = BigDecimal.TEN,
            paymentDate = currentDate,
            comment = "random comment",
            paymentAuthorized = true,
            paymentConfirmed = true,
            paymentSettlements = listOf(paymentSettlement)
        )
        private val paymentDetail = AdvancePaymentDetail(
            id = paymentId,
            projectId = projectId,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            projectVersion = version,
            partnerId = partnerId,
            partnerType = ProjectPartnerRole.PARTNER,
            partnerNumber = 5,
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
            paymentSettlements = listOf(paymentSettlement)
        )
    }

    @MockK
    lateinit var paymentPersistence: PaymentAdvancePersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var validator: AdvancePaymentValidator

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var updateAdvancePayment: UpdateAdvancePaymentDetail

    @BeforeEach
    fun resetMocks() {
        clearMocks(auditPublisher)
    }

    @Test
    fun `update advance payment new created`() {
        val newPayment = paymentUpdate.copy(
            id = null,
            paymentAuthorized = false,
            paymentConfirmed = false
        )
        val savedPayment = paymentDetail.copy(
            paymentAuthorized = false,
            paymentAuthorizedDate = null,
            paymentAuthorizedUser = null,
            paymentConfirmed = false,
            paymentConfirmedDate = null,
            paymentConfirmedUser = null
        )

        every { validator.validateDetail(newPayment, null) } returns Unit
        every { securityService.getUserIdOrThrow() } returns currentUserId
        val toSaveSlot = slot<AdvancePaymentUpdate>()
        every { paymentPersistence.updatePaymentDetail(capture(toSaveSlot)) } returns savedPayment

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } just Runs

        assertThat(updateAdvancePayment.updateDetail(newPayment)).isEqualTo(savedPayment)
        assertThat(toSaveSlot.captured).isEqualTo(
            paymentUpdate.copy(
            id = null,
            paymentAuthorized = false,
            paymentAuthorizedUserId = null,
            paymentAuthorizedDate = null,
            paymentConfirmed = false,
            paymentConfirmedUserId = null,
            paymentConfirmedDate = null
        ))
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.ADVANCE_PAYMENT_IS_CREATED)
        assertThat(auditSlot.captured.auditCandidate.description)
            .isEqualTo("Advance payment number $paymentId is created for partner PP5 for funding source (4, OTHER)")
    }

    @Test
    fun `update advance payment`() {
        val paymentSaved = paymentDetail.copy(
            paymentAuthorized = false,
            paymentConfirmed = false
        )
        every { paymentPersistence.getPaymentDetail(paymentId) } returns paymentSaved
        every { validator.validateDetail(paymentUpdate, paymentSaved) } returns Unit
        every { securityService.getUserIdOrThrow() } returns currentUserId
        val result = mockk<AdvancePaymentDetail>()
        every { result.id } returns paymentId
        every { result.projectId } returns projectId
        every { result.projectCustomIdentifier } returns paymentSaved.projectCustomIdentifier
        every { result.projectAcronym } returns paymentSaved.projectAcronym
        every { result.partnerId } returns partnerId
        every { result.partnerType } returns paymentSaved.partnerType
        every { result.partnerNumber } returns paymentSaved.partnerNumber
        every { result.programmeFund } returns paymentSaved.programmeFund
        every { result.amountPaid } returns BigDecimal.TEN
        val toUpdateSlot = slot<AdvancePaymentUpdate>()
        every {
            paymentPersistence.updatePaymentDetail(capture(toUpdateSlot))
        } returns result

        val auditSlot = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } just Runs

        assertThat(updateAdvancePayment.updateDetail(paymentUpdate)).isEqualTo(result)
        assertThat(toUpdateSlot.captured).isEqualTo(
            paymentUpdate.copy(
            paymentAuthorized = true,
            paymentAuthorizedUserId = currentUserId,
            paymentAuthorizedDate = currentDate,
            paymentConfirmed = true,
            paymentConfirmedUserId = currentUserId,
            paymentConfirmedDate = currentDate
        ))

        verify(exactly = 1) { auditPublisher.publishEvent(auditSlot[0]) }
        verify(exactly = 1) { auditPublisher.publishEvent(auditSlot[1]) }

        assertThat(auditSlot[0].auditCandidate.action).isEqualTo(AuditAction.ADVANCE_PAYMENT_DETAIL_AUTHORISED)
        assertThat(auditSlot[0].auditCandidate.description).isEqualTo("Amount 10.00 was authorised for Advance payment " +
            "details for advance payment $paymentId of partner PP5 for funding source (4, OTHER)")
        assertThat(auditSlot[1].auditCandidate.action).isEqualTo(AuditAction.ADVANCE_PAYMENT_DETAIL_CONFIRMED)
        assertThat(auditSlot[1].auditCandidate.description).isEqualTo("Amount 10.00 was confirmed for Advance payment " +
            "details for advance payment $paymentId of partner PP5 for funding source (4, OTHER)")
    }

    @Test
    fun `update advance payment - invalid data`() {
        every { paymentPersistence.getPaymentDetail(paymentId) } returns paymentDetail
        every {
            validator.validateDetail(paymentUpdate, paymentDetail)
        } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateAdvancePayment.updateDetail(paymentUpdate)
        }
    }


    @Test
    fun `update advance payment - unconfirm payment should not display audit log`() {
        val paymentUnconfirmed = paymentUpdate.copy(
            paymentConfirmed = false
        )
        every { paymentPersistence.getPaymentDetail(paymentId) } returns paymentDetail
        every { validator.validateDetail(paymentUnconfirmed, paymentDetail) } returns Unit
        every { securityService.getUserIdOrThrow() } returns currentUserId
        val result = mockk<AdvancePaymentDetail>()
        every { result.id } returns paymentId
        every { result.projectId } returns projectId
        every { result.projectCustomIdentifier } returns paymentDetail.projectCustomIdentifier
        every { result.projectAcronym } returns paymentDetail.projectAcronym
        every { result.partnerId } returns partnerId
        every { result.partnerType } returns paymentDetail.partnerType
        every { result.partnerNumber } returns paymentDetail.partnerNumber
        every { result.paymentSettlements } returns paymentDetail.paymentSettlements
        val toUpdateSlot = slot<AdvancePaymentUpdate>()
        every {
            paymentPersistence.updatePaymentDetail(capture(toUpdateSlot))
        } returns result

        val auditSlot = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } just Runs

        assertThat(updateAdvancePayment.updateDetail(paymentUnconfirmed)).isEqualTo(result)
        assertThat(toUpdateSlot.captured).isEqualTo(paymentUnconfirmed.copy(
            paymentAuthorized = true,
            paymentAuthorizedUserId = userId,
            paymentAuthorizedDate = currentDate.minusDays(3),
            paymentConfirmed = false,
            paymentConfirmedUserId = null,
            paymentConfirmedDate = null
        ))

        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @Test
    fun `update advance payment - unauthorise payment should not display audit log`() {
        val paymentUnconfirmed = paymentUpdate.copy(
            paymentAuthorized = false,
            paymentAuthorizedUserId = null,
            paymentAuthorizedDate = null,
            paymentConfirmed = false,
            paymentConfirmedUserId = null,
            paymentConfirmedDate = null
            )
        every { paymentPersistence.getPaymentDetail(paymentId) } returns paymentDetail
        every { validator.validateDetail(paymentUnconfirmed, paymentDetail) } returns Unit
        every { securityService.getUserIdOrThrow() } returns currentUserId
        val result = mockk<AdvancePaymentDetail>()
        every { result.id } returns paymentId
        every { result.projectId } returns projectId
        every { result.projectCustomIdentifier } returns paymentDetail.projectCustomIdentifier
        every { result.projectAcronym } returns paymentDetail.projectAcronym
        every { result.partnerId } returns partnerId
        every { result.partnerType } returns paymentDetail.partnerType
        every { result.partnerNumber } returns paymentDetail.partnerNumber
        val toUpdateSlot = slot<AdvancePaymentUpdate>()
        every {
            paymentPersistence.updatePaymentDetail(capture(toUpdateSlot))
        } returns result

        val auditSlot = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } just Runs

        assertThat(updateAdvancePayment.updateDetail(paymentUnconfirmed)).isEqualTo(result)
        assertThat(toUpdateSlot.captured).isEqualTo(paymentUnconfirmed.copy(
            paymentAuthorized = false,
            paymentAuthorizedUserId = null,
            paymentAuthorizedDate = null,
            paymentConfirmed = false,
            paymentConfirmedUserId = null,
            paymentConfirmedDate = null
        ))

        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }
}
