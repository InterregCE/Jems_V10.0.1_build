package io.cloudflight.jems.server.payments.service.applicationToEc

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcUpdate
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.updatePaymentApplicationToEcDetail.UpdatePaymentApplicationToEcDetail
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UpdatePaymentApplicationToEcTest : UnitTest() {

    companion object {
        private const val paymentApplicationsToEcId = 1L

        private val accountingYear = AccountingYear(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val fund = ProgrammeFund(id = 3L, selected = true)

        private val paymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = fund,
            accountingYear = accountingYear
        )

        private val paymentApplicationsToEcUpdate = PaymentApplicationToEcUpdate(
            id = paymentApplicationsToEcId,
            programmeFundId = fund.id,
            accountingYearId = accountingYear.id
        )

        private val paymentApplicationsToEcDetail = PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatus.Draft,
            paymentApplicationsToEcSummary = paymentApplicationsToEcSummary
        )

    }

    @MockK
    lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    lateinit var service: UpdatePaymentApplicationToEcDetail

    @Test
    fun createPaymentApplicationToEcTest() {
        every {
            paymentApplicationsToEcPersistence.updatePaymentApplicationToEc(paymentApplicationsToEcUpdate)
        } returns paymentApplicationsToEcDetail

        assertThat(service.updatePaymentApplicationToEc(paymentApplicationsToEcUpdate))
            .isEqualTo(paymentApplicationsToEcDetail)
    }
}

