package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.finalizePaymentApplicationToEc

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.ec.*
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
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

        private fun contractMonitoring(projectId: Long, answer: ContractingMonitoringExtendedOption) = ProjectContractingMonitoring(
            projectId = projectId,
            addDates = listOf(),
            dimensionCodes = listOf(),
            typologyPartnership = mockk(),
            typologyStrategic = mockk(),
            typologyProv94 = answer,
            typologyProv95 = answer
        )
    }

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @MockK
    lateinit var paymentPersistence: PaymentPersistence

    @MockK
    lateinit var contractingMonitoringPersistence : ContractingMonitoringPersistence

    @InjectMockKs
    lateinit var finalizePaymentApplicationToEc: FinalizePaymentApplicationToEc

    @BeforeEach
    fun reset() {
        clearMocks(auditPublisher, paymentApplicationsToEcPersistence)
    }

    @Test
    fun finalizePaymentApplicationToEc() {
        val finalizedPayment = paymentApplicationDetail(PaymentEcStatus.Finished)
        every {
            paymentApplicationsToEcPersistence.updatePaymentApplicationToEcStatus(
                PAYMENT_ID,
                PaymentEcStatus.Finished
            )
        } returns finalizedPayment
        every { paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(PAYMENT_ID) } returns paymentApplicationDetail(
            PaymentEcStatus.Draft
        )
        every { paymentApplicationsToEcPersistence.getPaymentsLinkedToEcPayment(PAYMENT_ID) } returns mapOf(
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
            paymentApplicationsToEcPersistence.calculateAndGetTotals(
                PAYMENT_ID
            )
        } returns paymentToEcAmountSummaryTmpMap

        every {
            paymentApplicationsToEcPersistence.saveTotalsWhenFinishingEcPayment(
                ecPaymentId = PAYMENT_ID,
                totals = paymentsIncludedInPaymentsToEcMapped
            )
        } returns Unit

        every { contractingMonitoringPersistence.getContractingMonitoring(99L) } returns contractMonitoring(
            projectId = 99L,
            answer = ContractingMonitoringExtendedOption.No
        )
        every { contractingMonitoringPersistence.getContractingMonitoring(100L) } returns contractMonitoring(
            projectId = 100L,
            answer = ContractingMonitoringExtendedOption.Yes
        )

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { paymentApplicationsToEcPersistence.existsDraftByFundAndAccountingYear(programmeFund.id, accountingYear.id,) } returns false

        val toUpdate = mapOf(
            14L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            15L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            16L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            17L to PaymentSearchRequestScoBasis.FallsUnderArticle94Or95
        )
        every { paymentApplicationsToEcPersistence.updatePaymentToEcFinalScoBasis(toUpdate) } returns Unit

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
        verify(exactly = 1) {  paymentApplicationsToEcPersistence.saveTotalsWhenFinishingEcPayment(
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
