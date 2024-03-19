package io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcList

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class GetPaymentApplicationToEcListTest : UnitTest() {

    @MockK
    lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    lateinit var service: GetPaymentApplicationToEcList

    @Test
    fun getPaymentApplicationToEcDetailTest() {
        val result = mockk<Page<PaymentApplicationToEc>>()
        every {
            paymentApplicationsToEcPersistence.findAll(Pageable.unpaged())
        } returns result

        assertThat(service.getPaymentApplicationsToEc(Pageable.unpaged())).isEqualTo(result)
    }

}

