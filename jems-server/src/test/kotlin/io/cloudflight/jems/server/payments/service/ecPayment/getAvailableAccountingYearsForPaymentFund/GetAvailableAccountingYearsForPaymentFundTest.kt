package io.cloudflight.jems.server.payments.service.ecPayment.getAvailableAccountingYearsForPaymentFund

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.AccountingYearAvailability
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetAvailableAccountingYearsForPaymentFundTest: UnitTest() {

    @MockK
    lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    lateinit var getAvailableAccountingYearsForPaymentFund: GetAvailableAccountingYearsForPaymentFund

    @Test
    fun getAvailableAccountingYearsForPaymentFund() {
        val result = mockk<List<AccountingYearAvailability>>()
        every { ecPaymentPersistence.getAvailableAccountingYearsForFund(1L) } returns result
        assertThat(getAvailableAccountingYearsForPaymentFund.getAvailableAccountingYearsForPaymentFund(1L))
            .isEqualTo(result)
    }
}
