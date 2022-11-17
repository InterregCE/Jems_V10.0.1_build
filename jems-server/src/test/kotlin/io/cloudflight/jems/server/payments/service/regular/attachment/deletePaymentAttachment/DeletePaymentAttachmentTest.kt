package io.cloudflight.jems.server.payments.service.regular.attachment.deletePaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.regular.PaymentRegularPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeletePaymentAttachmentTest : UnitTest() {

    @MockK
    lateinit var paymentPersistence: PaymentRegularPersistence

    @InjectMockKs
    lateinit var interactor: DeletePaymentAttachment

    @BeforeEach
    fun reset() {
        clearMocks(paymentPersistence)
    }

    @Test
    fun delete() {
        every { paymentPersistence.deletePaymentAttachment(15L) } answers { }
        interactor.delete(15L)
        verify(exactly = 1) { paymentPersistence.deletePaymentAttachment(15L) }
    }

}
