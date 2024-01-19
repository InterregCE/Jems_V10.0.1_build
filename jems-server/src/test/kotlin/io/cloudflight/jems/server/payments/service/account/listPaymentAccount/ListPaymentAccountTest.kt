package io.cloudflight.jems.server.payments.service.account.listPaymentAccount

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.expectedAccountsOverviewList
import io.cloudflight.jems.server.payments.service.account.paymentAccount
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListPaymentAccountTest : UnitTest() {

    @MockK
    lateinit var  paymentAccountPersistence: PaymentAccountPersistence

    @InjectMockKs
    lateinit var service: ListPaymentAccount

    @Test
    fun listAccounts() {
        every { paymentAccountPersistence.getAllAccounts() } returns listOf(paymentAccount)

        assertThat(service.listPaymentAccount()).isEqualTo(expectedAccountsOverviewList)
    }
}
