package io.cloudflight.jems.server.payments.service.applicationToEc

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationsToEc.GetPaymentApplicationsToEc
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class GetPaymentApplicationsToEcTest : UnitTest() {

    companion object {
        private const val paymentApplicationsToEcId = 1L

        private val accountingYear = AccountingYear(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val fund = ProgrammeFund(id = 3L, selected = true)

        private val paymentApplicationToEc = PaymentApplicationToEc(
            id = paymentApplicationsToEcId,
            programmeFund = fund,
            accountingYear = accountingYear,
            status = PaymentEcStatus.Draft
        )

    }

    @MockK
    lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    lateinit var service: GetPaymentApplicationsToEc

    @Test
    fun getPaymentApplicationToEcDetailTest() {
        every {
            paymentApplicationsToEcPersistence.findAll(Pageable.unpaged())
        } returns PageImpl(listOf(paymentApplicationToEc))

        assertThat(service.getPaymentApplicationsToEc(Pageable.unpaged()))
            .isEqualTo(PageImpl(listOf(paymentApplicationToEc)))
    }
}

