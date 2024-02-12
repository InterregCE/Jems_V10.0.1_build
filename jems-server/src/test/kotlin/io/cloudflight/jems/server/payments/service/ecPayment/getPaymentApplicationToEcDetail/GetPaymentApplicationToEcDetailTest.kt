package io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcDetail

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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

        private fun paymentApplicationsToEcDetail(status: PaymentEcStatus) = PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = status,
            paymentApplicationToEcSummary = paymentApplicationsToEcSummary
        )
    }

    @MockK private lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence
    @MockK private lateinit var paymentAccountPersistence: PaymentAccountPersistence

    @InjectMockKs private lateinit var service: GetPaymentApplicationToEcDetail

    @ParameterizedTest(name = "finalizePaymentApplicationToEc {0} when otherDraftExists {1} and yearOpen {2}")
    @CsvSource(value = [
        "Draft,false,DRAFT,false",
        "Draft,false,FINISHED,false",
        "Draft,true,DRAFT,false",
        "Draft,true,FINISHED,false",
        "Finished,false,DRAFT,true",
        "Finished,false,FINISHED,false",
        "Finished,true,DRAFT,false",
        "Finished,true,FINISHED,false",
    ])
    fun getPaymentApplicationToEcDetail(
        ecPaymentStatus: PaymentEcStatus,
        otherDraftExists: Boolean,
        yearStatus: PaymentAccountStatus,
        expectedFlag: Boolean,
    ) {
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(paymentApplicationsToEcId) } returns
                paymentApplicationsToEcDetail(ecPaymentStatus)

        every { ecPaymentPersistence.existsDraftByFundAndAccountingYear(fund.id, accountingYear.id) } returns otherDraftExists
        every { paymentAccountPersistence.findByFundAndYear(fund.id, accountingYear.id).status } returns yearStatus

        assertThat(service.getPaymentApplicationToEcDetail(paymentApplicationsToEcId)).isEqualTo(
            PaymentApplicationToEcDetail(
                id = paymentApplicationsToEcId,
                status = ecPaymentStatus,
                isAvailableToReOpen = expectedFlag,
                paymentApplicationToEcSummary = paymentApplicationsToEcSummary
            )
        )
    }
}
