package io.cloudflight.jems.server.payments.service.account.corrections.updateCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.service.account.corrections.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class UpdateLinkedCorrectionToPaymentAccountTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 541L
        private const val EC_PAYMENT_ID = 542L

        private val correctionUpdate = PaymentAccountCorrectionLinkingUpdate(
            correctedPrivateContribution = BigDecimal(205),
            correctedPublicContribution = BigDecimal(206),
            correctedAutoPublicContribution = BigDecimal(207),
            comment = "Updated comment",
            correctedFundAmount = BigDecimal.valueOf(80),
        )

        private val paymentAccountCorrectionExtension = PaymentAccountCorrectionExtension(
            correctionId = CORRECTION_ID,
            paymentAccountId = EC_PAYMENT_ID,
            paymentAccountStatus = PaymentAccountStatus.DRAFT,
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
        )

    }

    @MockK
    lateinit var correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence

    @InjectMockKs
    lateinit var service: UpdateLinkedCorrectionToPaymentAccount

    @Test
    fun updateLinkedCorrection() {
        every { correctionLinkingPersistence.getCorrectionExtension(CORRECTION_ID).paymentAccountStatus } returns PaymentAccountStatus.DRAFT
        every {
            correctionLinkingPersistence.updateCorrectionLinkedToPaymentAccountCorrectedAmounts(
                correctionId = CORRECTION_ID,
                correctionLinkingUpdate = correctionUpdate
            )
        } returns paymentAccountCorrectionExtension

        service.updateCorrection(correctionId = CORRECTION_ID, correctionLinkingUpdate = correctionUpdate)
        verify(exactly = 1) {
            correctionLinkingPersistence.updateCorrectionLinkedToPaymentAccountCorrectedAmounts(
                correctionId = CORRECTION_ID,
                correctionLinkingUpdate = correctionUpdate
            )
        }
    }

    @Test
    fun `updateLinkedCorrection - ec payment closed exception`() {
        every { correctionLinkingPersistence.getCorrectionExtension(CORRECTION_ID).paymentAccountStatus } returns PaymentAccountStatus.FINISHED
        assertThrows<PaymentAccountNotInDraftException> {
            service.updateCorrection(
                correctionId = CORRECTION_ID,
                correctionLinkingUpdate = correctionUpdate
            )
        }
    }

}

