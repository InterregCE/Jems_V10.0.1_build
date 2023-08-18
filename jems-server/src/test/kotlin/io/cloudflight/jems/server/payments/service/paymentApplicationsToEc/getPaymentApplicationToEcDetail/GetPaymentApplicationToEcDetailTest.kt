package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationToEcDetail

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetPaymentApplicationToEcDetailTest: UnitTest() {

    @MockK
    lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    lateinit var service: GetPaymentApplicationToEcDetail

    @Test
    fun getPaymentApplicationToEcDetail() {
        val result = mockk<PaymentApplicationToEcDetail>()
        every {
            paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(7L)
        } returns result

        assertThat(service.getPaymentApplicationToEcDetail(7L)).isEqualTo(result)
    }
}
