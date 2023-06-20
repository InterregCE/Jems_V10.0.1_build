package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.service.advance.getAdvancePayments.GetAdvancePayments
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class GetAdvancePaymentsTest: UnitTest() {

    @MockK
    private lateinit var paymentPersistence: PaymentAdvancePersistence

    @InjectMockKs
    private lateinit var getAdvancePayments: GetAdvancePayments


    @Test
    fun `list advance payments`() {
        val filters = mockk<AdvancePaymentSearchRequest>()
        val result = mockk<AdvancePayment>()

        every { paymentPersistence.list(Pageable.unpaged(), filters) } returns PageImpl(listOf(result))

        assertThat(getAdvancePayments.list(Pageable.unpaged(), filters)).contains(result)
    }

}
