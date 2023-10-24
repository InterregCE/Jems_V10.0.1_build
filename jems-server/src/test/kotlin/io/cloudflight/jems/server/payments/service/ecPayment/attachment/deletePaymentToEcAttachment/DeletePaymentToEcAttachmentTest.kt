package io.cloudflight.jems.server.payments.service.ecPayment.attachment.deletePaymentToEcAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeletePaymentToEcAttachmentTest : UnitTest() {

    @MockK
    lateinit var paymentToEcPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    lateinit var interactor: DeletePaymentToEcAttachment

    @BeforeEach
    fun reset() {
        clearMocks(paymentToEcPersistence)
    }

    @Test
    fun delete() {
        every { paymentToEcPersistence.deletePaymentToEcAttachment(15L) } answers { }
        interactor.delete(15L)
        verify(exactly = 1) { paymentToEcPersistence.deletePaymentToEcAttachment(15L) }
    }

}
