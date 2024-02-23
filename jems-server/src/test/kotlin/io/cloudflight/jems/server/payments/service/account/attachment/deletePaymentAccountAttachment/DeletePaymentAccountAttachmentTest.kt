package io.cloudflight.jems.server.payments.service.account.attachment.deletePaymentAccountAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeletePaymentAccountAttachmentTest : UnitTest() {

    @MockK
    lateinit var paymentAccountPersistence: PaymentAccountPersistence

    @InjectMockKs
    lateinit var interactor: DeletePaymentAccountAttachment

    @BeforeEach
    fun reset() {
        clearMocks(paymentAccountPersistence)
    }

    @Test
    fun delete() {
        every { paymentAccountPersistence.deletePaymentAccountAttachment(15L) } answers { }
        interactor.delete(15L)
        verify(exactly = 1) { paymentAccountPersistence.deletePaymentAccountAttachment(15L) }
    }

}
