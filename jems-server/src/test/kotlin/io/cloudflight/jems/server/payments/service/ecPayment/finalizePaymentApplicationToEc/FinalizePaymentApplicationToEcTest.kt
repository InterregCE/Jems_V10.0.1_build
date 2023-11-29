package io.cloudflight.jems.server.payments.service.ecPayment.finalizePaymentApplicationToEc

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.ec.CorrectionInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
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
        private const val PROJECT_ID = 101L
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

        private val includedInP01 = PaymentToEcAmountSummaryLine(
            priorityAxis = "PO1",
            totalEligibleExpenditure = BigDecimal(302),
            totalUnionContribution = BigDecimal(0),
            totalPublicContribution = BigDecimal(803),
        )
        private val includedInP02 = PaymentToEcAmountSummaryLine(
            priorityAxis = "PO2",
            totalEligibleExpenditure = BigDecimal(304),
            totalUnionContribution = BigDecimal(0),
            totalPublicContribution = BigDecimal(806),
        )

        private val paymentsIncludedInPaymentsToEcMapped = mapOf(
            PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to mapOf<Long?, PaymentToEcAmountSummaryLine>(
                25L to includedInP01,
                26L to includedInP02,
            ),
        )

        private val paymentToEcAmountSummaryTmpMap = mapOf(
            PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to mapOf<Long?, PaymentToEcAmountSummaryLineTmp>(
                25L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 25L,
                    priorityAxis = "PO1",
                    fundAmount = BigDecimal.valueOf(101),
                    partnerContribution = BigDecimal(201),
                    ofWhichPublic = BigDecimal(301),
                    ofWhichAutoPublic = BigDecimal(401),
                ),
                26L to PaymentToEcAmountSummaryLineTmp(
                    priorityId = 26L,
                    priorityAxis = "PO2",
                    fundAmount = BigDecimal.valueOf(102),
                    partnerContribution = BigDecimal(202),
                    ofWhichPublic = BigDecimal(302),
                    ofWhichAutoPublic = BigDecimal(402),
                )
            )
        )

        private val linkedCorrections = mapOf(
            21L to
                CorrectionInEcPaymentMetadata(
                    correctionId = 21L,
                    auditControlNr = 1,
                    correctionNr = 1,
                    projectId = PROJECT_ID,
                    finalScoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                    typologyProv94 = ContractingMonitoringExtendedOption.Yes,
                    typologyProv95 = ContractingMonitoringExtendedOption.No,
                ),
            22L to
                CorrectionInEcPaymentMetadata(
                    correctionId = 22L,
                    auditControlNr = 1,
                    correctionNr = 2,
                    projectId = PROJECT_ID,
                    finalScoBasis = PaymentSearchRequestScoBasis.FallsUnderArticle94Or95,
                    typologyProv94 = ContractingMonitoringExtendedOption.Yes,
                    typologyProv95 = ContractingMonitoringExtendedOption.No,
                )
        )
    }

    @MockK
    lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @MockK
    lateinit var ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence

    @MockK
    lateinit var ecPaymentCorrectionLinkPersistence: EcPaymentCorrectionLinkPersistence

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

        every { ecPaymentCorrectionLinkPersistence.getCorrectionsLinkedToEcPayment(PAYMENT_ID) } returns linkedCorrections

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { ecPaymentPersistence.existsDraftByFundAndAccountingYear(programmeFund.id, accountingYear.id,) } returns false

        val toUpdate = mapOf(
            14L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            15L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            16L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            17L to PaymentSearchRequestScoBasis.FallsUnderArticle94Or95
        )
        val correctionToUpdate =  mapOf(
            21L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            22L to PaymentSearchRequestScoBasis.FallsUnderArticle94Or95,
        )
        every { ecPaymentLinkPersistence.updatePaymentToEcFinalScoBasis(toUpdate) } returns Unit
        every { ecPaymentCorrectionLinkPersistence.updatePaymentToEcFinalScoBasis(correctionToUpdate) } returns Unit

        assertThat(finalizePaymentApplicationToEc.finalizePaymentApplicationToEc(PAYMENT_ID)).isEqualTo(
            paymentApplicationDetail(PaymentEcStatus.Finished, isAvailableToReOpen = true)
        )
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_APPLICATION_TO_EC_STATUS_CHANGED,
                description = "Payment application to EC number 3 created for Fund (10, OTHER) for accounting " +
                    "Year 1: 2021-01-01 - 2022-06-30 changes status from Draft to Finished " +
                    "and the following items were included:\nFTLS [14, 15, 17]\nRegular [16]\nCorrection [101_AC1.1, 101_AC1.2]"
            )
        )
        verify(exactly = 1) {  ecPaymentLinkPersistence.saveTotalsWhenFinishingEcPayment(
            ecPaymentId = PAYMENT_ID,
            totals = paymentsIncludedInPaymentsToEcMapped,
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
