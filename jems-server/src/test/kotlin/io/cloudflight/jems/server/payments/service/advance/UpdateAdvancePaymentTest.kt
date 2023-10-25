package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
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
        val newPayment = paymentUpdate.copy(id = null)
        val savedPayment = paymentDetail.copy(
            paymentAuthorized = false,
            paymentAuthorizedDate = null,
            paymentAuthorizedUser = null,
            paymentConfirmed = false,
            paymentConfirmedDate = null,
            paymentConfirmedUser = null
        )

        every { validator.validateDetail(newPayment, null) } returns Unit
        val toSaveSlot = slot<AdvancePaymentUpdate>()
        every { paymentPersistence.updatePaymentDetail(capture(toSaveSlot)) } returns savedPayment

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } just Runs

        assertThat(updateAdvancePayment.updateDetail(newPayment)).isEqualTo(savedPayment)
        assertThat(toSaveSlot.captured).isEqualTo(
            paymentUpdate.copy(
            id = null
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
        val result = mockk<AdvancePaymentDetail>()
        every { result.id } returns paymentId
        every { result.projectId } returns projectId
        every { result.projectCustomIdentifier } returns paymentSaved.projectCustomIdentifier
        every { result.projectAcronym } returns paymentSaved.projectAcronym
        every { result.partnerId } returns partnerId
        every { result.partnerType } returns paymentSaved.partnerType
        every { result.partnerNumber } returns paymentSaved.partnerNumber
        every { result.programmeFund } returns paymentSaved.programmeFund
        val toUpdateSlot = slot<AdvancePaymentUpdate>()
        every {
            paymentPersistence.updatePaymentDetail(capture(toUpdateSlot))
        } returns result

        assertThat(updateAdvancePayment.updateDetail(paymentUpdate)).isEqualTo(result)
        assertThat(toUpdateSlot.captured).isEqualTo(paymentUpdate)
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
}
