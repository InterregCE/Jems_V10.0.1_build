package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.updatePayment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class UpdateLinkedPaymentTest : UnitTest() {

    @MockK
    private lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    private lateinit var interactor: UpdateLinkedPayment

    @BeforeEach
    fun reset() {
        clearMocks(paymentApplicationsToEcPersistence)
    }

    @ParameterizedTest(name = "updateLinkedPayment {0}")
    @EnumSource(value = PaymentEcStatus::class, names = ["Draft"])
    fun updateLinkedPayment(status: PaymentEcStatus) {
        val paymentExtension = mockk<PaymentToEcExtension>()
        every { paymentExtension.ecPaymentStatus } returns status
        every { paymentApplicationsToEcPersistence.getPaymentExtension(82L) } returns paymentExtension

        val slotToUpdate = slot<PaymentToEcLinkingUpdate>()
        every { paymentApplicationsToEcPersistence.updatePaymentToEcCorrectedAmounts(82L, capture(slotToUpdate)) } answers { }

        val newValues = mockk<PaymentToEcLinkingUpdate>()
        interactor.updateLinkedPayment(82L, newValues)

        assertThat(slotToUpdate.captured).isEqualTo(newValues)
    }

    @ParameterizedTest(name = "updateLinkedPayment - ec payment not draft {0}")
    @EnumSource(value = PaymentEcStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `updateLinkedPayment - ec payment not draft`(status: PaymentEcStatus) {
        val paymentExtension = mockk<PaymentToEcExtension>()
        every { paymentExtension.ecPaymentStatus } returns status
        every { paymentApplicationsToEcPersistence.getPaymentExtension(83L) } returns paymentExtension

        assertThrows<PaymentApplicationToEcNotInDraftException> {
            interactor.updateLinkedPayment(83L, mockk())
        }
    }

}
