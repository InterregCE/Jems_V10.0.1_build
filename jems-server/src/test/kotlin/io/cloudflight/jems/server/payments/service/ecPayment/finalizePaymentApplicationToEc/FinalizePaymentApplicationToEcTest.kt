package io.cloudflight.jems.server.payments.service.ecPayment.finalizePaymentApplicationToEc

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.ec.*
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
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


        private fun paymentApplicationDetail(status: PaymentEcStatus, isAvailableToReOpen: Boolean = false) = PaymentApplicationToEcDetail(
            id = PAYMENT_ID,
            status = status,
            isAvailableToReOpen = isAvailableToReOpen,
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
    lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @MockK
    lateinit var ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var finalizePaymentApplicationToEc: FinalizePaymentApplicationToEc

    @BeforeEach
    fun reset() {
        clearMocks(ecPaymentPersistence, ecPaymentLinkPersistence, auditPublisher)
    }

    @Test
    fun finalizePaymentApplicationToEc() {
        val finalizedPayment = paymentApplicationDetail(PaymentEcStatus.Finished)
        every {
            ecPaymentPersistence.updatePaymentApplicationToEcStatus(
                PAYMENT_ID,
                PaymentEcStatus.Finished
            )
        } returns finalizedPayment
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(PAYMENT_ID) } returns paymentApplicationDetail(
            PaymentEcStatus.Draft
        )
        every { ecPaymentLinkPersistence.getPaymentsLinkedToEcPayment(PAYMENT_ID) } returns mapOf(
            14L to PaymentInEcPaymentMetadata(
                14L,
                PaymentType.FTLS,
                PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                ContractingMonitoringExtendedOption.No,
                ContractingMonitoringExtendedOption.No
            ),
            15L to PaymentInEcPaymentMetadata(
                15L,
                PaymentType.FTLS,
                PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                ContractingMonitoringExtendedOption.No,
                ContractingMonitoringExtendedOption.No
            ),
            16L to PaymentInEcPaymentMetadata(
                16L,
                PaymentType.REGULAR,
                PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                ContractingMonitoringExtendedOption.No,
                ContractingMonitoringExtendedOption.No
            ),
            17L to PaymentInEcPaymentMetadata(
                17,
                PaymentType.FTLS,
                PaymentSearchRequestScoBasis.FallsUnderArticle94Or95,
                ContractingMonitoringExtendedOption.Yes,
                ContractingMonitoringExtendedOption.No
            ),
        )
        every {
            ecPaymentLinkPersistence.calculateAndGetOverview(
                PAYMENT_ID
            )
        } returns paymentToEcAmountSummaryTmpMap

        every {
            ecPaymentLinkPersistence.saveTotalsWhenFinishingEcPayment(
                ecPaymentId = PAYMENT_ID,
                totals = paymentsIncludedInPaymentsToEcMapped
            )
        } returns Unit

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { ecPaymentPersistence.existsDraftByFundAndAccountingYear(programmeFund.id, accountingYear.id,) } returns false

        val toUpdate = mapOf(
            14L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            15L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            16L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            17L to PaymentSearchRequestScoBasis.FallsUnderArticle94Or95
        )
        every { ecPaymentLinkPersistence.updatePaymentToEcFinalScoBasis(toUpdate) } returns Unit

        assertThat(finalizePaymentApplicationToEc.finalizePaymentApplicationToEc(PAYMENT_ID)).isEqualTo(
            paymentApplicationDetail(PaymentEcStatus.Finished, isAvailableToReOpen = true)
        )
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_APPLICATION_TO_EC_STATUS_CHANGED,
                description = "Payment application to EC number 3 created for Fund (10, OTHER) for accounting " +
                    "Year 1: 2021-01-01 - 2022-06-30 changes status from Draft to Finished " +
                    "and the following items were included:\nFTLS [14, 15, 17]\nRegular [16]"
            )
        )
        verify(exactly = 1) {  ecPaymentLinkPersistence.saveTotalsWhenFinishingEcPayment(
            ecPaymentId = PAYMENT_ID,
            totals = mapOf(Pair(PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95, paymentsIncludedInPaymentsToEc))
        ) }
    }

    @Test
    fun `finalizePaymentApplicationToEc wrong status - should throw PaymentApplicationToEcNotInDraftException`() {
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(PAYMENT_ID) } returns paymentApplicationDetail(
            PaymentEcStatus.Finished
        )

        assertThrows<PaymentApplicationToEcNotInDraftException> {
            finalizePaymentApplicationToEc.finalizePaymentApplicationToEc(
                PAYMENT_ID
            )
        }
    }

}
