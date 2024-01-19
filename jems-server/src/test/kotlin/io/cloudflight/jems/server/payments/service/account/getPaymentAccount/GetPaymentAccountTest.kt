package io.cloudflight.jems.server.payments.service.account.getPaymentAccount

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.account.PAYMENT_ACCOUNT_ID
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.paymentAccount
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetPaymentAccountTest: UnitTest() {

    @MockK
    lateinit var  paymentAccountPersistence: PaymentAccountPersistence

    @InjectMockKs
    lateinit var service: GetPaymentAccount

    @Test
    fun getPaymentAccount() {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount

        assertThat(service.getPaymentAccount(PAYMENT_ACCOUNT_ID)).isEqualTo(paymentAccount)
    }

}
