package io.cloudflight.jems.server.payments.service.advance.updateStatus

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentStatus
import io.cloudflight.jems.server.payments.service.advance.AdvancePaymentValidator
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

class UpdateAdvancePaymentStatusTest : UnitTest() {

    companion object {
        private const val paymentId = 1L
        private const val partnerId = 2L
        private const val projectId = 3L
        private const val currentUserId = 7L
        private const val fundId = 4L
        private const val userId = 6L
        private const val version = "1.0"
        private val currentDate = ZonedDateTime.now().toLocalDate()
        private val fund = ProgrammeFund(
            id = fundId,
            selected = true,
            abbreviation = setOf(InputTranslation(SystemLanguage.EN, "FUND")),
        )
        private val advancePaymentDetail = AdvancePaymentDetail(
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
            paymentSettlements = listOf()
        )
    }

    @MockK
    lateinit var advancePaymentPersistence: PaymentAdvancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var advancePaymentValidator: AdvancePaymentValidator

    @InjectMockKs
    lateinit var interactor: UpdateAdvancePaymentStatus

    @BeforeEach
    fun setup() {
        clearMocks(advancePaymentPersistence, auditPublisher, securityService, advancePaymentValidator)
    }

    @Test
    fun updateStatusToAuthorized() {
        val advancePayment = advancePaymentDetail.copy(paymentAuthorized = false, paymentConfirmed = false)
        every { advancePaymentPersistence.getPaymentDetail(paymentId) } returns advancePayment
        every { advancePaymentValidator.validateStatus(AdvancePaymentStatus.AUTHORIZED, advancePayment) } just Runs
        every { securityService.getUserIdOrThrow() } returns currentUserId
        every {
            advancePaymentPersistence.updateAdvancePaymentStatus(paymentId, status = AdvancePaymentStatus.AUTHORIZED, currentUserId = currentUserId)
        } returns advancePaymentDetail.copy(paymentAuthorized = true, paymentConfirmed = false)
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } just Runs

        interactor.updateStatus(paymentId = paymentId, status = AdvancePaymentStatus.AUTHORIZED)

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.ADVANCE_PAYMENT_DETAIL_AUTHORISED)
        assertThat(auditSlot.captured.auditCandidate.description)
            .isEqualTo("Advance payment details for advance payment $paymentId of partner PP5 for funding source (4, OTHER) are authorised")
    }

    @Test
    fun updateStatusToConfirmed() {
        val advancePayment = advancePaymentDetail.copy(paymentAuthorized = true, paymentConfirmed = false)
        every { advancePaymentPersistence.getPaymentDetail(paymentId) } returns advancePayment
        every { advancePaymentValidator.validateStatus(AdvancePaymentStatus.CONFIRMED, advancePayment) } just Runs
        every { securityService.getUserIdOrThrow() } returns currentUserId
        every {
            advancePaymentPersistence.updateAdvancePaymentStatus(paymentId, status = AdvancePaymentStatus.CONFIRMED, currentUserId = currentUserId)
        } returns advancePaymentDetail.copy(paymentAuthorized = true, paymentConfirmed = true)
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } just Runs

        interactor.updateStatus(paymentId = paymentId, status = AdvancePaymentStatus.CONFIRMED)

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.ADVANCE_PAYMENT_DETAIL_CONFIRMED)
        assertThat(auditSlot.captured.auditCandidate.description)
            .isEqualTo("Advance payment details for advance payment $paymentId of partner PP5 for funding source (4, OTHER) are confirmed")
    }

}
