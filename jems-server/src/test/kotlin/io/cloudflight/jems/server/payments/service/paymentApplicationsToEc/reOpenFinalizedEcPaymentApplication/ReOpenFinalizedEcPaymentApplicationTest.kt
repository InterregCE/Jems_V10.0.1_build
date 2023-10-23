package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.reOpenFinalizedEcPaymentApplication

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.createPaymentApplicationToEc.EcPaymentApplicationSameFundAccountingYearExistsException
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
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


    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    lateinit var service: ReOpenFinalizedEcPaymentApplication



    @Test
    fun reOpenFinishedEcPaymentApplication() {
        every { paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(ecPaymentApplicationId) } returns paymentApplicationsToEcDetail(
            ecPaymentApplicationId,
            PaymentEcStatus.Finished
        )
        every { paymentApplicationsToEcPersistence.existsDraftByFundAndAccountingYear(fund.id, accountingYear.id) } returns false

        every {
            paymentApplicationsToEcPersistence.updatePaymentApplicationToEcStatus(any(), any())
        } returns paymentApplicationsToEcDetail(
            ecPaymentApplicationId, PaymentEcStatus.Draft
        )

        every { paymentApplicationsToEcPersistence.getPaymentsLinkedToEcPayment(ecPaymentApplicationId) } returns emptyMap()

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot))} answers { }

        assertThat(service.reOpen(ecPaymentApplicationId)).isEqualTo(
            paymentApplicationsToEcDetail(ecPaymentApplicationId, PaymentEcStatus.Draft)
        )


        val expectedAuditCandidate = AuditCandidate(
            action= AuditAction.PAYMENT_APPLICATION_TO_EC_STATUS_CHANGED,
            project=null,
            entityRelatedId=null,
            description="Payment application to EC number 3 created for Fund (1, ERDF) " +
                    "for accounting Year 1: 2021-01-01 - 2022-06-30 changes status from Finished to Draft"
        )

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(expectedAuditCandidate)
    }


    @Test
    fun `ReOpen Finished throws - existing Draft with same Fund and accounting year`() {
        every { paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(ecPaymentApplicationId) } returns paymentApplicationsToEcDetail(
            ecPaymentApplicationId,
            PaymentEcStatus.Finished
        )
        every { paymentApplicationsToEcPersistence.existsDraftByFundAndAccountingYear(fund.id, accountingYear.id) } returns true

        assertThrows<EcPaymentApplicationSameFundAccountingYearExistsException> { service.reOpen(ecPaymentApplicationId) }
    }

    @Test
    fun `ReOpen Draft throws - payment application not Finished`() {
        every { paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(ecPaymentApplicationId) } returns paymentApplicationsToEcDetail(
            ecPaymentApplicationId,
            PaymentEcStatus.Draft
        )

        assertThrows<EcPaymentApplicationNotFinishedException> { service.reOpen(ecPaymentApplicationId) }
    }
 }
