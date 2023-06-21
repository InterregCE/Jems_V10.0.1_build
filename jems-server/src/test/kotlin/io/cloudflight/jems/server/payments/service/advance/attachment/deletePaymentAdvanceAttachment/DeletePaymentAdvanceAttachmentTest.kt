package io.cloudflight.jems.server.payments.service.regular.attachment.deletePaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.payments.service.advance.attachment.deletePaymentAdvanceAttachment.DeletePaymentAdvanceAttachment
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeletePaymentAdvanceAttachmentTest : UnitTest() {

    @MockK
    lateinit var paymentPersistence: PaymentAdvancePersistence

    @InjectMockKs
    lateinit var interactor: DeletePaymentAdvanceAttachment

    @BeforeEach
    fun reset() {
        clearMocks(paymentPersistence)
    }

    @Test
    fun delete() {
        every { paymentPersistence.deletePaymentAdvanceAttachment(15L) } answers { }
        interactor.delete(15L)
        verify(exactly = 1) { paymentPersistence.deletePaymentAdvanceAttachment(15L) }
    }

}
