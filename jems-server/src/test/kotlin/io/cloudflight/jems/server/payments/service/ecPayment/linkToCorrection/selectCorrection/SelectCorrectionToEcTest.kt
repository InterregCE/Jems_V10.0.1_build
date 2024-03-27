package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.selectCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionExtension
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.deselectPayment.PaymentApplicationToEcNotInDraftException
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class SelectCorrectionToEcTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 541L
        private const val EC_PAYMENT_ID = 20L
        private const val FUND_ID = 21L

        val paymentDraft = mockk<PaymentApplicationToEcDetail>()

        private val paymentToEcExtensionModel = PaymentToEcCorrectionExtension(
            correctionId = CORRECTION_ID,
            ecPaymentId = null,
            ecPaymentStatus = PaymentEcStatus.Draft,
            auditControlStatus = AuditControlStatus.Ongoing,
            comment = "Comment",
            fundAmount = BigDecimal.valueOf(25.80),
            publicContribution = BigDecimal.valueOf(35.00),
            correctedPublicContribution = BigDecimal.valueOf(36.20),
            autoPublicContribution = BigDecimal.valueOf(15.00),
            correctedAutoPublicContribution = BigDecimal.valueOf(16.00),
            privateContribution = BigDecimal.valueOf(45.00),
            correctedPrivateContribution = BigDecimal.valueOf(46.20),
            correctedFundAmount = BigDecimal.valueOf(75.00),
            unionContribution = BigDecimal.valueOf(0.00),
            correctedTotalEligibleWithoutArt94or95 = BigDecimal.valueOf(46.00),
            correctedUnionContribution = BigDecimal.valueOf(80.50),
            totalEligibleWithoutArt94or95 = BigDecimal.valueOf(90.00),
        )

    }

    @MockK
    lateinit var ecPaymentCorrectionLinkPersistence: EcPaymentCorrectionLinkPersistence

    @MockK
    lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    lateinit var service: SelectCorrectionToEcPayment

    @Test
    fun selectCorrection() {
        every { paymentDraft.status } returns PaymentEcStatus.Draft
        every { paymentDraft.paymentApplicationToEcSummary.programmeFund.id } returns FUND_ID
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(EC_PAYMENT_ID) } returns paymentDraft
        every { ecPaymentCorrectionLinkPersistence.getCorrectionExtension(CORRECTION_ID) } returns paymentToEcExtensionModel
        every { ecPaymentCorrectionLinkPersistence.getCorrectionIdsAvailableForEcPayments(FUND_ID) } returns setOf(
            CORRECTION_ID
        )

        every {
            ecPaymentCorrectionLinkPersistence.selectCorrectionToEcPayment(
                correctionIds = setOf(
                    CORRECTION_ID
                ), ecPaymentId = EC_PAYMENT_ID
            )
        } returns Unit

        service.selectCorrectionToEcPayment(correctionId = CORRECTION_ID, ecPaymentId = EC_PAYMENT_ID)
        verify(exactly = 1) {
            ecPaymentCorrectionLinkPersistence.selectCorrectionToEcPayment(
                correctionIds = setOf(
                    CORRECTION_ID
                ), ecPaymentId = EC_PAYMENT_ID
            )
        }
    }

    @Test
    fun `selectCorrection - ec payment closed exception`() {
        every { paymentDraft.status } returns PaymentEcStatus.Finished
        every { paymentDraft.paymentApplicationToEcSummary.programmeFund.id } returns FUND_ID

        assertThrows<PaymentApplicationToEcNotInDraftException> {
            service.selectCorrectionToEcPayment(
                correctionId = CORRECTION_ID,
                ecPaymentId = EC_PAYMENT_ID
            )
        }
    }

    @Test
    fun `selectCorrection - correction not available to be selected exception`() {
        every { paymentDraft.status } returns PaymentEcStatus.Draft
        every { paymentDraft.paymentApplicationToEcSummary.programmeFund.id } returns FUND_ID
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(EC_PAYMENT_ID) } returns paymentDraft
        every { ecPaymentCorrectionLinkPersistence.getCorrectionExtension(CORRECTION_ID).ecPaymentId } returns null
        every { ecPaymentCorrectionLinkPersistence.getCorrectionIdsAvailableForEcPayments(FUND_ID) } returns setOf(
            CORRECTION_ID + 1L
        )

        every {
            ecPaymentCorrectionLinkPersistence.selectCorrectionToEcPayment(
                correctionIds = setOf(
                    CORRECTION_ID
                ), ecPaymentId = EC_PAYMENT_ID
            )
        } returns Unit

        assertThrows<CorrectionNotAvailableForSelectionException> {
            service.selectCorrectionToEcPayment(
                correctionId = CORRECTION_ID,
                ecPaymentId = EC_PAYMENT_ID
            )
        }
    }

}
