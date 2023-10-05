package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.finalizePaymentApplicationToEc

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate

class FinalizePaymentApplicationToEcTest : UnitTest() {

    companion object {
        private const val PAYMENT_ID = 3L
        private const val programmeFundId = 10L
        private const val accountingYearId = 3L
        private val submissionDate = LocalDate.now()

        private val programmeFund = ProgrammeFund(programmeFundId, true)

        private val accountingYear =
            AccountingYear(accountingYearId, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))

        private val expectedPaymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = programmeFund,
            accountingYear = accountingYear,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private fun paymentApplicationDetail(status: PaymentEcStatus) = PaymentApplicationToEcDetail(
            id = PAYMENT_ID,
            status = status,
            paymentApplicationToEcSummary = expectedPaymentApplicationsToEcSummary
        )

        private val paymentsIncludedInPaymentsToEc = listOf(
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO1",
                totalEligibleExpenditure = BigDecimal(302),
                totalUnionContribution = BigDecimal(0),
                totalPublicContribution = BigDecimal(803)
            ),
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO2",
                totalEligibleExpenditure = BigDecimal(304),
                totalUnionContribution = BigDecimal(0),
                totalPublicContribution = BigDecimal(806)
            ),
        )

        private val paymentsIncludedInPaymentsToEcMapped = mapOf(
            Pair(
                PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                paymentsIncludedInPaymentsToEc
            )
        )

        private val paymentToEcAmountSummaryTmpMap = mapOf(
            Pair(
                PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95, listOf(
                    PaymentToEcAmountSummaryLineTmp(
                        priorityAxis = "PO1",
                        fundAmount = BigDecimal.valueOf(101),
                        partnerContribution = BigDecimal(201),
                        ofWhichPublic = BigDecimal(301),
                        ofWhichAutoPublic = BigDecimal(401)
                    ),
                    PaymentToEcAmountSummaryLineTmp(
                        priorityAxis = "PO2",
                        fundAmount = BigDecimal.valueOf(102),
                        partnerContribution = BigDecimal(202),
                        ofWhichPublic = BigDecimal(302),
                        ofWhichAutoPublic = BigDecimal(402)
                    )
                )
            )
        )

    }

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @MockK
    lateinit var paymentPersistence: PaymentPersistence

    @InjectMockKs
    lateinit var finalizePaymentApplicationToEc: FinalizePaymentApplicationToEc

    @BeforeEach
    fun reset() {
        clearMocks(auditPublisher, paymentApplicationsToEcPersistence)
    }

    @Test
    fun finalizePaymentApplicationToEc() {
        val finalizedPayment = paymentApplicationDetail(PaymentEcStatus.Finished)
        val currentPayment = paymentApplicationDetail(
            PaymentEcStatus.Draft
        )

        every { paymentApplicationsToEcPersistence.finalizePaymentApplicationToEc(PAYMENT_ID) } returns finalizedPayment
        every { paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(PAYMENT_ID) } returns currentPayment
        every { paymentApplicationsToEcPersistence.getPaymentsLinkedToEcPayment(PAYMENT_ID) } returns mapOf(
            14L to PaymentType.FTLS,
            15L to PaymentType.FTLS,
            16L to PaymentType.REGULAR,
            17L to PaymentType.FTLS,
        )
        every {
            paymentApplicationsToEcPersistence.getSelectedPaymentsToEcPayment(
                PAYMENT_ID
            )
        } returns paymentToEcAmountSummaryTmpMap

        every {
            paymentApplicationsToEcPersistence.saveCumulativeAmountsByType(
                ecPaymentId = PAYMENT_ID,
                totals = paymentsIncludedInPaymentsToEcMapped
            )
        } returns Unit

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        assertThat(finalizePaymentApplicationToEc.finalizePaymentApplicationToEc(PAYMENT_ID)).isEqualTo(PaymentEcStatus.Finished)
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_APPLICATION_TO_EC_STATUS_CHANGED,
                description = "Payment application to EC number 3 created for Fund (10, OTHER) for accounting " +
                    "Year 1: 2021-01-01 - 2022-06-30 changes status from Draft to Finished " +
                    "and the following items were included:\nFTLS [14, 15, 17]\nRegular [16]"
            )
        )
        verify(exactly = 1) {  paymentApplicationsToEcPersistence.saveCumulativeAmountsByType(
            ecPaymentId = PAYMENT_ID,
            totals = mapOf(Pair(PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95, paymentsIncludedInPaymentsToEc))
        ) }
    }

    @Test
    fun `finalizePaymentApplicationToEc wrong status - should throw PaymentApplicationToEcNotInDraftException`() {
        every { paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(PAYMENT_ID) } returns paymentApplicationDetail(
            PaymentEcStatus.Finished
        )

        assertThrows<PaymentApplicationToEcNotInDraftException> {
            finalizePaymentApplicationToEc.finalizePaymentApplicationToEc(
                PAYMENT_ID
            )
        }
    }

}
