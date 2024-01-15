package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.deselectCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.deselectPayment.PaymentApplicationToEcNotInDraftException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeselectCorrectionFromEcTest: UnitTest() {

    companion object {
        private const val CORRECTION_ID = 541L
    }

    @MockK
    lateinit var ecPaymentCorrectionLinkPersistence: EcPaymentCorrectionLinkPersistence

    @InjectMockKs
    lateinit var service: DeselectCorrectionFromEc

    @Test
    fun deselectCorrection() {
        every {  ecPaymentCorrectionLinkPersistence.getCorrectionExtension(CORRECTION_ID).ecPaymentStatus } returns PaymentEcStatus.Draft
        every { ecPaymentCorrectionLinkPersistence.deselectCorrectionFromEcPaymentAndResetFields(setOf(CORRECTION_ID)) } answers { }
        service.deselectCorrectionFromEcPayment(correctionId = CORRECTION_ID)
        verify(exactly = 1) { ecPaymentCorrectionLinkPersistence.deselectCorrectionFromEcPaymentAndResetFields(setOf(CORRECTION_ID)) }
    }

    @Test
    fun `deselectCorrection - ec payment closed exception`() {
        every {  ecPaymentCorrectionLinkPersistence.getCorrectionExtension(CORRECTION_ID).ecPaymentStatus } returns PaymentEcStatus.Finished
        assertThrows<PaymentApplicationToEcNotInDraftException> { service.deselectCorrectionFromEcPayment(correctionId = CORRECTION_ID) }
    }
}
