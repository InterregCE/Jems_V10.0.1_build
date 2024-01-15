package io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcDetail

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class GetPaymentApplicationToEcDetailTest: UnitTest() {

    companion object {
        private const val paymentApplicationsToEcId = 7L
        private val accountingYear = AccountingYear(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val fund = ProgrammeFund(id = 3L, selected = true)
        private val submissionDate = LocalDate.now()
        private val paymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = fund,
            accountingYear = accountingYear,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val paymentApplicationsToEcDetail = PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatus.Draft,
            paymentApplicationToEcSummary = paymentApplicationsToEcSummary
        )
    }

    @MockK
    lateinit var paymentApplicationsToEcPersistence: PaymentApplicationToEcPersistence

    @InjectMockKs
    lateinit var service: GetPaymentApplicationToEcDetail

    @Test
    fun getPaymentApplicationToEcDetail() {
        val expected =  PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatus.Draft,
            paymentApplicationToEcSummary = paymentApplicationsToEcSummary
        )
        every {
            paymentApplicationsToEcPersistence.getPaymentApplicationToEcDetail(paymentApplicationsToEcId)
        } returns paymentApplicationsToEcDetail
        every { paymentApplicationsToEcPersistence.existsDraftByFundAndAccountingYear(fund.id, accountingYear.id) } returns false

        assertThat(service.getPaymentApplicationToEcDetail(paymentApplicationsToEcId)).isEqualTo(expected)
    }
}
