package io.cloudflight.jems.server.payments.service.ecPayment.reOpenFinalizedEcPaymentApplication

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate

class ReOpenFinalizedEcPaymentApplicationTest: UnitTest() {

    companion object {
        private const val ecPaymentApplicationId = 3L
        private val accountingYear = AccountingYear(1L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val fund = ERDF_FUND
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

        private fun paymentApplicationsToEcDetail(
            id: Long,
            status: PaymentEcStatus,
            summary: PaymentApplicationToEcSummary = paymentApplicationsToEcSummary
        ) = PaymentApplicationToEcDetail(
            id = id,
            status = status,
            paymentApplicationToEcSummary = summary
        )
    }

    @MockK private lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence
    @MockK private lateinit var paymentAccountPersistence: PaymentAccountPersistence
    @MockK private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var service: ReOpenFinalizedEcPaymentApplication

    @BeforeEach
    fun resetMocks() {
        clearMocks(ecPaymentPersistence, paymentAccountPersistence, auditPublisher)
    }

    @Test
    fun reOpenFinishedEcPaymentApplication() {
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(ecPaymentApplicationId) } returns mockk {
            every { status } returns PaymentEcStatus.Finished
            every { paymentApplicationToEcSummary.programmeFund.id } returns 18L
            every { paymentApplicationToEcSummary.accountingYear.id } returns 52L
        }
        every { ecPaymentPersistence.existsDraftByFundAndAccountingYear(18L, 52L) } returns false
        every { paymentAccountPersistence.findByFundAndYear(fundId = 18L, accountingYearId = 52L).status } returns PaymentAccountStatus.DRAFT

        val result = paymentApplicationsToEcDetail(ecPaymentApplicationId, PaymentEcStatus.Draft)
        every {
            ecPaymentPersistence.updatePaymentApplicationToEcStatus(ecPaymentApplicationId, PaymentEcStatus.Draft)
        } returns result

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot))} answers { }

        assertThat(service.reOpen(ecPaymentApplicationId)).isEqualTo(result)

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action= AuditAction.PAYMENT_APPLICATION_TO_EC_STATUS_CHANGED,
                project=null,
                entityRelatedId=null,
                description="Payment application to EC number 3 created for Fund (1, ERDF) " +
                        "for accounting Year 1: 2021-01-01 - 2022-06-30 changes status from Finished to Draft"
            )
        )
    }

    @Test
    fun `reOpen - accounting year is finished`() {
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(18L) } returns mockk {
            every { status } returns PaymentEcStatus.Finished
            every { paymentApplicationToEcSummary.programmeFund.id } returns 15L
            every { paymentApplicationToEcSummary.accountingYear.id } returns 66L
        }
        every { ecPaymentPersistence.existsDraftByFundAndAccountingYear(15L, 66L) } returns false
        every { paymentAccountPersistence.findByFundAndYear(fundId = 15L, accountingYearId = 66L).status } returns PaymentAccountStatus.FINISHED

        assertThrows<AccountingYearHasBeenAlreadyFinishedException> { service.reOpen(18L) }

        verify(exactly = 0) { ecPaymentPersistence.updatePaymentApplicationToEc(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @Test
    fun `reOpen - there is other draft already`() {
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(10L) } returns mockk {
            every { status } returns PaymentEcStatus.Finished
            every { paymentApplicationToEcSummary.programmeFund.id } returns 12L
            every { paymentApplicationToEcSummary.accountingYear.id } returns 71L
        }
        every { ecPaymentPersistence.existsDraftByFundAndAccountingYear(12L, 71L) } returns true

        assertThrows<ThereIsOtherEcPaymentInDraftException> { service.reOpen(10L) }

        verify(exactly = 0) { ecPaymentPersistence.updatePaymentApplicationToEc(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @Test
    fun `reOpen - ec payment is not finished`() {
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(12L).status } returns PaymentEcStatus.Draft

        assertThrows<EcPaymentNotFinishedException> { service.reOpen(12L) }

        verify(exactly = 0) { ecPaymentPersistence.updatePaymentApplicationToEc(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

}
