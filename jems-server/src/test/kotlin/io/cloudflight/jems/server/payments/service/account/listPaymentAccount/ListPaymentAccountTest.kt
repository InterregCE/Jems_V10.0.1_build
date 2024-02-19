package io.cloudflight.jems.server.payments.service.account.listPaymentAccount

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.expectedAccountsOverviewList
import io.cloudflight.jems.server.payments.service.account.finance.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.payments.service.account.paymentAccount
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListPaymentAccountTest : UnitTest() {

    @MockK
    lateinit var  paymentAccountPersistence: PaymentAccountPersistence

    @MockK
    lateinit var paymentAccountFinancePersistence: PaymentAccountFinancePersistence

    @InjectMockKs
    lateinit var service: ListPaymentAccount

    @Test
    fun listAccounts() {
        every { paymentAccountPersistence.getAllAccounts() } returns listOf(paymentAccount)
        every { paymentAccountFinancePersistence.getOverviewTotalsForFinishedPaymentAccounts() } returns emptyMap()

        assertThat(service.listPaymentAccount()).isEqualTo(expectedAccountsOverviewList)
    }
}
