package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.updateCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment.PaymentApplicationToEcNotInDraftException
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class UpdateLinkedCorrectionTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 541L
        private const val EC_PAYMENT_ID = 542L

        private val correctionUpdate = PaymentToEcCorrectionLinkingUpdate(
            correctedPrivateContribution = BigDecimal(205),
            correctedPublicContribution = BigDecimal(206),
            correctedAutoPublicContribution = BigDecimal(207),
            comment = "Updated comment",
            correctedFundAmount = BigDecimal.valueOf(80),
            correctedTotalEligibleWithoutArt94or95 = BigDecimal.valueOf(40),
            correctedUnionContribution = BigDecimal.valueOf(95),
        )

        private val paymentToEcExtensionModel = PaymentToEcCorrectionExtension(
            correctionId = CORRECTION_ID,
            ecPaymentId = EC_PAYMENT_ID,
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

    @InjectMockKs
    lateinit var service: UpdateLinkedCorrectionToEcPayment

    @Test
    fun updateLinkedCorrection() {
        every { ecPaymentCorrectionLinkPersistence.getCorrectionExtension(CORRECTION_ID).ecPaymentStatus } returns PaymentEcStatus.Draft
        every {
            ecPaymentCorrectionLinkPersistence.updateCorrectionLinkedToEcPaymentCorrectedAmounts(
                correctionId = CORRECTION_ID,
                ecPaymentCorrectionLinkingUpdate = correctionUpdate
            )
        } returns paymentToEcExtensionModel

        service.updateLinkedCorrection(correctionId = CORRECTION_ID, updateLinkedCorrection = correctionUpdate)
        verify(exactly = 1) {
            ecPaymentCorrectionLinkPersistence.updateCorrectionLinkedToEcPaymentCorrectedAmounts(
                correctionId = CORRECTION_ID,
                ecPaymentCorrectionLinkingUpdate = correctionUpdate
            )
        }
    }

    @Test
    fun `updateLinkedCorrection - ec payment closed exception`() {
        every { ecPaymentCorrectionLinkPersistence.getCorrectionExtension(CORRECTION_ID).ecPaymentStatus } returns PaymentEcStatus.Finished
        assertThrows<PaymentApplicationToEcNotInDraftException> {
            service.updateLinkedCorrection(
                correctionId = CORRECTION_ID,
                updateLinkedCorrection = correctionUpdate
            )
        }
    }

}
