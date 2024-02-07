package io.cloudflight.jems.server.payments.service.account.corrections.deselectCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.service.account.corrections.PaymentAccountCorrectionLinkingPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
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

    @BeforeEach
    fun resetMocks() {
        clearMocks(correctionLinkingPersistence)
    }

    @Test
    fun deselectCorrection() {
        every { correctionLinkingPersistence.getCorrectionExtension(CORRECTION_ID).paymentAccountStatus } returns PaymentAccountStatus.DRAFT
        every { correctionLinkingPersistence.deselectCorrectionFromPaymentAccountAndResetFields(CORRECTION_ID) } answers { }

        service.deselectCorrection(correctionId = CORRECTION_ID)
        verify(exactly = 1) { correctionLinkingPersistence.deselectCorrectionFromPaymentAccountAndResetFields(CORRECTION_ID) }
    }

    @Test
    fun `deselectCorrection - wrong status`() {
        every { correctionLinkingPersistence.getCorrectionExtension(545L).paymentAccountStatus } returns PaymentAccountStatus.FINISHED
        assertThrows<PaymentAccountNotInDraftException> { service.deselectCorrection(correctionId = 545L) }
        verify(exactly = 0) { correctionLinkingPersistence.deselectCorrectionFromPaymentAccountAndResetFields(any()) }
    }

    @Test
    fun `deselectCorrection - no status`() {
        every { correctionLinkingPersistence.getCorrectionExtension(547L).paymentAccountStatus } returns null
        assertThrows<PaymentAccountNotInDraftException> { service.deselectCorrection(correctionId = 547L) }
        verify(exactly = 0) { correctionLinkingPersistence.deselectCorrectionFromPaymentAccountAndResetFields(any()) }
    }

}
