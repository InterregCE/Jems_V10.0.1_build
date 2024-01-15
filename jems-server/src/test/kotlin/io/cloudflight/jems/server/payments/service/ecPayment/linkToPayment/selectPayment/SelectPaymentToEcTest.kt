package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.selectPayment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
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

internal class SelectPaymentToEcTest : UnitTest() {

    @MockK
    private lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @MockK
    private lateinit var ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence

    @InjectMockKs
    private lateinit var interactor: SelectPaymentToEc

    @BeforeEach
    fun reset() {
        clearMocks(ecPaymentPersistence, ecPaymentLinkPersistence)
    }

    @ParameterizedTest(name = "selectPaymentToEcPayment {0}")
    @EnumSource(value = PaymentEcStatus::class, names = ["Draft"])
    fun selectPaymentToEcPayment(status: PaymentEcStatus) {
        val payment = mockk<PaymentApplicationToEcDetail>()
        every { payment.status } returns status
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(45L) } returns payment

        val paymentExtension = mockk<PaymentToEcExtension>()
        every { paymentExtension.ecPaymentId } returns null
        every { ecPaymentLinkPersistence.getPaymentExtension(82L) } returns paymentExtension

        val slotPaymentIds = slot<Set<Long>>()
        every { ecPaymentLinkPersistence.selectPaymentToEcPayment(paymentIds = capture(slotPaymentIds), ecPaymentId = 45L) } answers { }

        interactor.selectPaymentToEcPayment(paymentId = 82L, ecPaymentId = 45L)

        assertThat(slotPaymentIds.captured).containsExactly(82L)
    }

    @ParameterizedTest(name = "selectPaymentToEcPayment - ec payment taken {0}")
    @EnumSource(value = PaymentEcStatus::class, names = ["Draft"])
    fun `selectPaymentToEcPayment - ec payment taken`(status: PaymentEcStatus) {
        val payment = mockk<PaymentApplicationToEcDetail>()
        every { payment.status } returns status
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(46L) } returns payment

        val paymentExtension = mockk<PaymentToEcExtension>()
        every { paymentExtension.ecPaymentId } returns 999L // taken
        every { ecPaymentLinkPersistence.getPaymentExtension(81L) } returns paymentExtension

        val ex = assertThrows<PaymentApplicationAlreadyTakenException> {
            interactor.selectPaymentToEcPayment(paymentId = 81L, ecPaymentId = 46L)
        }
        assertThat(ex.i18nMessage.i18nArguments).containsEntry("ecPaymentId", "999")
    }

    @ParameterizedTest(name = "selectPaymentToEcPayment - ec payment not draft {0}")
    @EnumSource(value = PaymentEcStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `selectPaymentToEcPayment - ec payment not draft`(status: PaymentEcStatus) {
        val payment = mockk<PaymentApplicationToEcDetail>()
        every { payment.status } returns status
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(47L) } returns payment

        assertThrows<PaymentApplicationToEcNotInDraftException> {
            interactor.selectPaymentToEcPayment(paymentId = 0L, ecPaymentId = 47L)
        }
    }

}
