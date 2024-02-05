package io.cloudflight.jems.server.payments.service.account.corrections.deselectCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.service.account.corrections.PaymentAccountCorrectionLinkingPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeselectCorrectionFromPaymentAccountTest: UnitTest() {

    companion object {
        private const val CORRECTION_ID = 542L
    }

    @MockK
    lateinit var correctionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence

    @InjectMockKs
    lateinit var service: DeselectCorrectionFromPaymentAccount

    @Test
    fun deselectCorrection() {
        every {  correctionLinkingPersistence.getCorrectionExtension(CORRECTION_ID).paymentAccountStatus } returns PaymentAccountStatus.DRAFT
        every { correctionLinkingPersistence.deselectCorrectionFromPaymentAccountAndResetFields(setOf(CORRECTION_ID)) } answers { }
        service.deselectCorrection(correctionId = CORRECTION_ID)
        verify(exactly = 1) { correctionLinkingPersistence.deselectCorrectionFromPaymentAccountAndResetFields(setOf(CORRECTION_ID)) }
    }

    @Test
    fun `deselectCorrection - payment account finished exception`() {
        every {  correctionLinkingPersistence.getCorrectionExtension(CORRECTION_ID).paymentAccountStatus } returns PaymentAccountStatus.FINISHED
        assertThrows<PaymentAccountNotInDraftException> { service.deselectCorrection(correctionId = CORRECTION_ID) }
    }
}
