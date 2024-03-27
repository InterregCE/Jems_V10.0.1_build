package io.cloudflight.jems.server.payments.service.account.updatePaymentAccount

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.payments.service.account.PAYMENT_ACCOUNT_ID
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.expectedPaymentAccountUpdate
import io.cloudflight.jems.server.payments.service.account.paymentAccountUpdate
import io.cloudflight.jems.server.payments.service.account.paymentAccountUpdateWithErrors
import io.cloudflight.jems.server.payments.service.account.paymentAccountUpdated
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdatePaymentAccountTest: UnitTest()  {

    @MockK
    lateinit var paymentAccountPersistence: PaymentAccountPersistence

    @InjectMockKs
    lateinit var generalValidator: GeneralValidatorDefaultImpl

    @InjectMockKs
    lateinit var service: UpdatePaymentAccount

    @BeforeEach
    fun setup() {
        clearMocks(paymentAccountPersistence)
    }

    @Test
    fun updatePaymentAccount() {
        every{ paymentAccountPersistence.updatePaymentAccount(PAYMENT_ACCOUNT_ID, paymentAccountUpdate) } returns paymentAccountUpdated

        assertThat(service.updatePaymentAccount(PAYMENT_ACCOUNT_ID, paymentAccountUpdate)).isEqualTo(expectedPaymentAccountUpdate)
    }

    @Test
    fun `updatePaymentAccount - throw validation exception`() {
        assertThrows<AppInputValidationException> { service.updatePaymentAccount(PAYMENT_ACCOUNT_ID, paymentAccountUpdateWithErrors) }
    }

}
