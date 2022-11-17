package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.advance.getAdvancePaymentDetail.GetAdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetAdvancePaymentDetailTest: UnitTest() {

    companion object {
        private const val paymentId = 2L
    }

    @MockK
    lateinit var paymentPersistence: PaymentAdvancePersistence

    @InjectMockKs
    lateinit var getAdvancePaymentDetail: GetAdvancePaymentDetail

    @Test
    fun getPaymentDetail() {
        val mockResult = mockk<AdvancePaymentDetail>()
        every { paymentPersistence.getPaymentDetail(paymentId) } returns mockResult
        assertThat(getAdvancePaymentDetail.getPaymentDetail(paymentId)).isEqualTo(mockResult)
    }
}
