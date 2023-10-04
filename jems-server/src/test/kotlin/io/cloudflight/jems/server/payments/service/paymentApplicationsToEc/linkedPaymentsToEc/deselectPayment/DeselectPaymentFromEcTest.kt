package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.deselectPayment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
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

internal class DeselectPaymentFromEcTest : UnitTest() {

    @MockK
    private lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    private lateinit var interactor: DeselectPaymentFromEc

    @BeforeEach
    fun reset() {
        clearMocks(paymentApplicationsToEcPersistence)
    }

    @ParameterizedTest(name = "deselectPaymentFromEcPayment {0}")
    @EnumSource(value = PaymentEcStatus::class, names = ["Draft"])
    fun deselectPaymentFromEcPayment(status: PaymentEcStatus) {
        val paymentExtension = mockk<PaymentToEcExtension>()
        every { paymentExtension.ecPaymentStatus } returns status
        every { paymentApplicationsToEcPersistence.getPaymentExtension(51L) } returns paymentExtension

        val slotPaymentId = slot<Long>()
        every { paymentApplicationsToEcPersistence.deselectPaymentFromEcPaymentAndResetFields(capture(slotPaymentId)) } answers { }

        interactor.deselectPaymentFromEcPayment(paymentId = 51L)

        assertThat(slotPaymentId.captured).isEqualTo(51L)
    }

    @ParameterizedTest(name = "deselectPaymentFromEcPayment - ec payment not draft {0}")
    @EnumSource(value = PaymentEcStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `deselectPaymentFromEcPayment - ec payment not draft`(status: PaymentEcStatus) {
        val paymentExtension = mockk<PaymentToEcExtension>()
        every { paymentExtension.ecPaymentStatus } returns status
        every { paymentApplicationsToEcPersistence.getPaymentExtension(52L) } returns paymentExtension

        assertThrows<PaymentApplicationToEcNotInDraftException> {
            interactor.deselectPaymentFromEcPayment(paymentId = 52L)
        }
    }

}
