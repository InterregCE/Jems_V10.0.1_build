package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.deletePaymentApplicationToEc

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate

class DeletePaymentApplicationToEcTest : UnitTest() {

    companion object {
        private const val paymentApplicationsToEcId = 1L

        private val accountingYear = AccountingYear(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val fund = ProgrammeFund(id = 3L, selected = true)
        private val submissionDate = LocalDate.now()

        private val paymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = fund,
            accountingYear = accountingYear,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val paymentApplicationsToEcDetail = PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatus.Draft,
            paymentApplicationToEcSummary = paymentApplicationsToEcSummary
        )

    }

    @MockK
    lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var service: DeletePaymentApplicationToEc

    @BeforeEach
    fun resetMocks() {
        clearMocks(auditPublisher, paymentApplicationsToEcPersistence)
    }

    @Test
    fun deleteById() {
        every {
            paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(paymentApplicationsToEcId)
        } returns paymentApplicationsToEcDetail

        every {
            paymentApplicationsToEcPersistence.deleteById(paymentApplicationsToEcId)
        } answers {}

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        service.deleteById(paymentApplicationsToEcId)

        verify(exactly = 1) { paymentApplicationsToEcPersistence.deleteById(paymentApplicationsToEcId) }
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        Assertions.assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_APPLICATION_TO_EC_IS_DELETED,
                description = "Payment application to EC number 1 created for Fund (3, OTHER) for accounting Year 1: 2021-01-01 - 2022-06-30 was deleted"
            )
        )
    }
}
