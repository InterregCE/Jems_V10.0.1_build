package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForPayment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AvailableCorrectionsForPayment
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class GetAvailableCorrectionsForPaymentTest: UnitTest() {

    @MockK
    lateinit var paymentPersistence: PaymentPersistence

    @MockK
    lateinit var correctionsPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    lateinit var interactor: GetAvailableCorrectionsForPayment

    @Test
    fun getAvailableCorrections() {
        val corrections = listOf(mockk<AvailableCorrectionsForPayment>())
        val payment = mockk<PaymentDetail> {
            every { projectId } returns 1L
            every { partnerPayments } returns listOf(mockk {
                every { partnerId } returns 2L
            })
        }
        every { paymentPersistence.getPaymentDetails(3L) } returns payment
        every { correctionsPersistence.getAvailableCorrectionsForPayments(1L) } returns corrections

        Assertions.assertThat(interactor.getAvailableCorrections(3L)).isEqualTo(corrections)
    }
}
